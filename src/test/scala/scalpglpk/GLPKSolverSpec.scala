package scalpglpk

import scalpi._
import scalpi.variable._
import org.specs2.mutable._

class GLPKSolverSpec extends Specification { 
  "The GLPK Solver" should { 
    "return hard coded values" in { 
      val problem: ProblemDescription = null
      val solution = GLPKSolver.solve(problem)
      solution.targetValue should be equalTo 0.0
      solution.variableValues should be equalTo List(RealVariableValue(0.0))
    }
  }
}
