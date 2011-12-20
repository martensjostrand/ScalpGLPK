import sbt._

name := "ScalpGLPK"

version := "0.1-SNAPSHOT"

organization := "me.marten"

scalaVersion := "2.9.1"

libraryDependencies ++= Seq(
    "org.specs2" %% "specs2" % "1.6.1" % "test",
    "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
    "me.marten" %% "scalpi" % "0.1-SNAPSHOT",
    "org.gnu.glpk" % "glpk-java" % "1.0.19")

resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
                  "releases"  at "http://scala-tools.org/repo-releases", 
		  "XypronRelease" at "http://rsync.xypron.de/repository")

scalacOptions ++= Seq("-deprecation", "-unchecked")

// Prevent sbt to run test in parallel.
parallelExecution in Test := false

// Prevent Specs 2 from running specs in parallel.
testOptions in Test += Tests.Argument("sequential")







