import sbt._
import Keys._

object Build extends sbt.Build
{
  import Dependencies._

  lazy val myProject = Project("lineup", file(".")).settings(
    resolvers ++= Dependencies.resolutionRepos,

    libraryDependencies
      ++= compile(finagleCore, finagleHttp, sprayJson)
      ++  test(scalatest)
      ++  runtime(slf4j, logback),

    organization  := "com.olchovy",
    version       := "0.1.0",
    scalaVersion  := "2.9.2",
    scalacOptions := Seq("-deprecation", "-encoding", "utf8"),

    mainClass in (Compile, run) := Some("com.olchovy.lineup.cli.Boot")
  )
}
