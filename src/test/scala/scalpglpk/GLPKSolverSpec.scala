package scalpglpk

import scalpi._
import scalpi.variable._
import scalpi.constraint._
import scalpi.targetfunction.Maximize

import org.specs2.mutable._

import org.gnu.glpk.GLPK

class GLPKSolverSpec extends Specification { 
  val solver = GLPKSolver

  "The GLPK Solver" should { 

    "Use correct version of GLPK" in { 
      GLPK.glp_version() must be equalTo "4.45"
    }

    """Solve Maximize z =  17 * x1 + 12* x2
         subject to
           10 x1 + 7 x2 <= 40
           x1 +   x2 <=  5
         where,
           0.0 <= x1  integer
           0.0 <= x2  integer
    """ in {
      val x1 = IntegerVariable() withLowerBound(0)
      val x2 = IntegerVariable() withLowerBound(0)
      val c1 = Constraint(List(10, 7), ConstraintType.LT, 40)
      val c2 = Constraint(List(1, 1), ConstraintType.LT, 5)
      val target = Maximize(List(17, 12))
      val problem = ProblemDescription(target, List(c1, c2), List(x1, x2))
      val solution = solver.solve(problem)

      solution.targetValue must be equalTo 68.0
      solution.variableValues must be equalTo List(IntegerVariableValue(4), IntegerVariableValue(0))
    }
  }
}
