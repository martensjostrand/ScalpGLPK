package scalpglpk

import scalpi._
import scalpi.variable._
import scalpi.targetfunction._
import scalpi.constraint._

import org.gnu.glpk._

object GLPKSolver extends ProblemSolver{ 
  override def solve(p: ProblemDescription): ProblemSolution = { 
    val lp = GLPK.glp_create_prob()
    GLPK.glp_term_hook(null, null);
    GLPK.glp_set_prob_name(lp, "Problem")
    
    // Variables:
    GLPK.glp_add_cols(lp, p.variables.size)
    var variableIndex = 1
    p.variables.foreach{ variable =>
      GLPK.glp_set_col_name(lp, variableIndex, "x" + variableIndex)
      val columnKind = variable match { 
	case IntegerVariable() => { 
	  println("creating an integer variable from " + variable)
	  GLPKConstants.GLP_IV
	}
	case RealVariable() => { 
	  println("creating an continious variable from " + variable)
	  GLPKConstants.GLP_CV
	}
	case BinaryVariable() => { 
	  println("creating an binary variable from " + variable)
	  GLPKConstants.GLP_BV
	}
	case someVariable => throw new IllegalArgumentException("Unknown variable: " + someVariable)
      }

      GLPK.glp_set_col_kind(lp, variableIndex, columnKind)
      val variableConf = variable match { 
	case boundedVariable: BoundedVariable[_] => { 
	  val (hasUpperBound, upperBound) = boundedVariable.getUpperBound match { 
	    case Some(bound: Double) => (true, bound)
	    case None => (false, 0.0)
	  }
	  val (hasLowerBound, lowerBound) = boundedVariable.getLowerBound match { 
	    case Some(bound: Double) => (true, bound)
	    case Some(bound: Int) => (true, bound.toDouble)
	    case None => (false, 0.0)
	  }
	  
	  val ONLY_LOWER_BOUND = (true, false)
	  val ONLY_UPPER_BOUND = (false, true)
          val DOUBLE_BOUND = (true, true)
          val UNBOUNDED = (false, false)

	  (hasLowerBound, hasUpperBound) match { 
	    case ONLY_LOWER_BOUND => {
	      GLPK.glp_set_col_bnds(lp, variableIndex, GLPKConstants.GLP_LO, lowerBound, 0);
	    }
	    case ONLY_UPPER_BOUND => { 
	      GLPK.glp_set_col_bnds(lp, variableIndex, GLPKConstants.GLP_UP, 0, upperBound);
	    } 
	    case DOUBLE_BOUND => {
	      if(lowerBound == upperBound) { 
		GLPK.glp_set_col_bnds(lp, variableIndex, GLPKConstants.GLP_FX, lowerBound, upperBound);
	      } else { 
		GLPK.glp_set_col_bnds(lp, variableIndex, GLPKConstants.GLP_DB, lowerBound, upperBound)
	      }
	    }
	    case UNBOUNDED => { 
	      GLPK.glp_set_col_bnds(lp, variableIndex, GLPKConstants.GLP_FR, 0, 0);
	    }
	  }
	}
	case BinaryVariable() => { 
	  GLPK.glp_set_col_bnds(lp, variableIndex, GLPKConstants.GLP_DB, 0, 1)
	}
	case _ => {
	  GLPK.glp_set_col_bnds(lp, variableIndex, GLPKConstants.GLP_FR, 0, 0)
	}
      }
      variableIndex = variableIndex + 1
    }

    // Constraints
    GLPK.glp_add_rows(lp, p.constraints.size)
    var constraintIndex = 1;
    p.constraints.foreach{ constraint =>
      println("Creating constraints from " + constraint)
      GLPK.glp_set_row_name(lp, constraintIndex, "c" + constraintIndex)
      constraint.constraintType match { 
	case ConstraintType.Eq => {
	  println("Setting constraint type to GLP_FX")
	  GLPK.glp_set_row_bnds(lp, constraintIndex, GLPKConstants.GLP_FX, constraint.rhs, constraint.rhs)
	}
	case ConstraintType.LT => {
	  println("Setting constraint type to GLP_UP")
	  GLPK.glp_set_row_bnds(lp, constraintIndex, GLPKConstants.GLP_UP, 0.0,  constraint.rhs)
	}
	case ConstraintType.GT => {
	  println("Setting constraint type to GLP_LO")
	  GLPK.glp_set_row_bnds(lp, constraintIndex, GLPKConstants.GLP_LO, constraint.rhs, 0.0)
	}
      }
      
      val ind = GLPK.new_intArray(constraint.coefficients.size + 1);
      val values = GLPK.new_doubleArray(constraint.coefficients.size + 1)
      var coefIndex = 1;
      constraint.coefficients.foreach{ value =>
	GLPK.intArray_setitem(ind, coefIndex, coefIndex);
	GLPK.doubleArray_setitem(values, coefIndex, value)
	coefIndex = coefIndex + 1
      }
      GLPK.glp_set_mat_row(lp, constraintIndex, constraint.coefficients.size, ind, values);
      constraintIndex = constraintIndex + 1
    }

    // Target function
    GLPK.glp_set_obj_name(lp, "obj");
    val direction = p.targetFunction match { 
      case Maximize(_) =>  GLPKConstants.GLP_MAX
      case _ => GLPKConstants.GLP_MIN
    }
    GLPK.glp_set_obj_dir(lp,direction)
    GLPK.glp_set_obj_coef(lp, 0, 0)
    var targetIndex = 1;
    val coeffs = p.targetFunction match { 
      case max: Maximize => { 
	max.coefficients
      }
      case min: Minimize => { 
	min.coefficients
      }
    }

    coeffs.foreach{ coeff =>
      GLPK.glp_set_obj_coef(lp, targetIndex, coeff)
      targetIndex = targetIndex + 1
    }

    val iocp = new glp_iocp();
    iocp.setMsg_lev(GLPKConstants.GLP_MSG_ALL)
    GLPK.glp_init_iocp(iocp);
    iocp.setPresolve(GLPKConstants.GLP_ON);

    GLPK._glp_lpx_print_prob(lp, "theProblem.txt")

    val returnValue  = GLPK.glp_intopt(lp, iocp);
    val optimum = GLPK.glp_mip_obj_val(lp)

    var variableSolutionIndex = 0;
    val variableValues = p.variables.map{variable => 
      variableSolutionIndex = variableSolutionIndex + 1
      variable match { 
	case BinaryVariable() => BinaryVariableValue(false);
	case RealVariable() => RealVariableValue(GLPK.glp_mip_col_val(lp, variableSolutionIndex))
	case IntegerVariable() => IntegerVariableValue(GLPK.glp_mip_col_val(lp, variableSolutionIndex).toInt)
      }
      // GLPK.glp_mip_col_val(lp, c.getColumnNumber())
    }

    GLPK.glp_delete_prob(lp)
    ProblemSolution(optimum, variableValues)
  } 
}
