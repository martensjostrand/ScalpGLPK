package scalpglpk

import org.specs2.mutable._

import org.gnu.glpk.GLPK
import org.gnu.glpk.GLPKConstants
import org.gnu.glpk.SWIGTYPE_p_double
import org.gnu.glpk.SWIGTYPE_p_int
import org.gnu.glpk.glp_prob
import org.gnu.glpk.glp_iocp

class GLPKSpec extends Specification { 

  "The GLKP" should { 
    """Solve Maximize z =  17 * x1 + 12* x2
         subject to
           10 x1 + 7 x2 <= 40
           x1 +   x2 <=  5
         where,
           0.0 <= x1  integer
           0.0 <= x2  integer""" in { 
	     //  Create problem    
	     val lp = GLPK.glp_create_prob();
	     System.out.println("Problem created");
	     GLPK.glp_set_prob_name(lp, "myProblem");
	     
	     //  Define columns
	     GLPK.glp_add_cols(lp, 2);
	     GLPK.glp_set_col_name(lp, 1, "x1");
	     GLPK.glp_set_col_kind(lp, 1, GLPKConstants.GLP_IV);
	     GLPK.glp_set_col_bnds(lp, 1, GLPKConstants.GLP_LO, 0, 0);
	     GLPK.glp_set_col_name(lp, 2, "x2");
	     GLPK.glp_set_col_kind(lp, 2, GLPKConstants.GLP_IV);
	     GLPK.glp_set_col_bnds(lp, 2, GLPKConstants.GLP_LO, 0, 0);
	     
	     //  Create constraints
	     GLPK.glp_add_rows(lp, 2); 
	     GLPK.glp_set_row_name(lp, 1, "c1");
	     GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_UP, 0, 40);
	     var ind = GLPK.new_intArray(3);
	     GLPK.intArray_setitem(ind, 1, 1);
	     GLPK.intArray_setitem(ind, 2, 2);
	     var value = GLPK.new_doubleArray(3);
	     GLPK.doubleArray_setitem(value, 1, 10);
	     GLPK.doubleArray_setitem(value, 2,  7);
	     GLPK.glp_set_mat_row(lp, 1, 2, ind, value);
	     
	     ind = GLPK.new_intArray(3);
	     GLPK.intArray_setitem(ind, 1, 1);
	     GLPK.intArray_setitem(ind, 2, 2);
	     value = GLPK.new_doubleArray(3);
	     GLPK.glp_set_row_name(lp, 2, "c2");
	     GLPK.glp_set_row_bnds(lp, 2, GLPKConstants.GLP_UP, 0, 5);
	     GLPK.doubleArray_setitem(value, 1, 1);
	     GLPK.doubleArray_setitem(value, 2, 1);
	     GLPK.glp_set_mat_row(lp, 2, 2, ind, value);
	     
	     //  Define objective 
	     GLPK.glp_set_obj_name(lp, "obj");
	     GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
	     GLPK.glp_set_obj_coef(lp, 0, 0);
	     GLPK.glp_set_obj_coef(lp, 1, 17);
	     GLPK.glp_set_obj_coef(lp, 2, 12);
	     
	     //  solve model
	     val iocp = new glp_iocp();
	     GLPK.glp_init_iocp(iocp);
	     iocp.setPresolve(GLPKConstants.GLP_ON);
	     //  GLPK.glp_write_lp(lp, null, "yi.lp");
	     val ret = GLPK.glp_intopt(lp, iocp);
	     
	     val optimum = GLPK.glp_mip_obj_val(lp)
    
	     // free memory
	     GLPK.glp_delete_prob(lp);
	     optimum  must be equalTo 68.0
	}
    "Do it again" in { 
      	     //  Create problem    
	     val lp = GLPK.glp_create_prob();
	     System.out.println("Problem created");
	     GLPK.glp_set_prob_name(lp, "myProblem");
	     
	     //  Define columns
	     GLPK.glp_add_cols(lp, 2);
	     GLPK.glp_set_col_name(lp, 1, "x1");
	     GLPK.glp_set_col_kind(lp, 1, GLPKConstants.GLP_IV);
	     GLPK.glp_set_col_bnds(lp, 1, GLPKConstants.GLP_LO, 0, 0);
	     GLPK.glp_set_col_name(lp, 2, "x2");
	     GLPK.glp_set_col_kind(lp, 2, GLPKConstants.GLP_IV);
	     GLPK.glp_set_col_bnds(lp, 2, GLPKConstants.GLP_LO, 0, 0);
	     
	     //  Create constraints
	     GLPK.glp_add_rows(lp, 2); 
	     GLPK.glp_set_row_name(lp, 1, "c1");
	     GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_UP, 0, 40);
	     var ind = GLPK.new_intArray(3);
	     GLPK.intArray_setitem(ind, 1, 1);
	     GLPK.intArray_setitem(ind, 2, 2);
	     var value = GLPK.new_doubleArray(3);
	     GLPK.doubleArray_setitem(value, 1, 10);
	     GLPK.doubleArray_setitem(value, 2,  7);
	     GLPK.glp_set_mat_row(lp, 1, 2, ind, value);
	     
	     ind = GLPK.new_intArray(3);
	     GLPK.intArray_setitem(ind, 1, 1);
	     GLPK.intArray_setitem(ind, 2, 2);
	     value = GLPK.new_doubleArray(3);
	     GLPK.glp_set_row_name(lp, 2, "c2");
	     GLPK.glp_set_row_bnds(lp, 2, GLPKConstants.GLP_UP, 0, 5);
	     GLPK.doubleArray_setitem(value, 1, 1);
	     GLPK.doubleArray_setitem(value, 2, 1);
	     GLPK.glp_set_mat_row(lp, 2, 2, ind, value);
	     
	     //  Define objective 
	     GLPK.glp_set_obj_name(lp, "obj");
	     GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
	     GLPK.glp_set_obj_coef(lp, 0, 0);
	     GLPK.glp_set_obj_coef(lp, 1, 17);
	     GLPK.glp_set_obj_coef(lp, 2, 12);
	     
	     //  solve model
	     val iocp = new glp_iocp();
	     GLPK.glp_init_iocp(iocp);
	     iocp.setPresolve(GLPKConstants.GLP_ON);
	     //  GLPK.glp_write_lp(lp, null, "yi.lp");
	     val ret = GLPK.glp_intopt(lp, iocp);
	     
	     val optimum = GLPK.glp_mip_obj_val(lp)
    
	     // free memory
	     GLPK.glp_delete_prob(lp);
	     optimum  must be equalTo 68.0
    }
  }
}
