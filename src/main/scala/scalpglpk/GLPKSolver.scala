package scalpglpk

import scalpi._
import scalpi.variable._

object GLPKSolver extends ProblemSolver{ 
  override def solve(p: ProblemDescription): ProblemSolution = { 
    ProblemSolution(0.0, List(RealVariableValue(0.0)))
  } 
}
