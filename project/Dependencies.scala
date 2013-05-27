import sbt._

object Dependencies
{
  val resolutionRepos = Seq(
    ScalaToolsSnapshots,
    "Typesafe repo [releases]" at "http://repo.typesafe.com/typesafe/releases/",
    "spray repo" at "http://repo.spray.cc/"
  )

  def compile   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "compile")
  def provided  (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "provided")
  def test      (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "test")
  def runtime   (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "runtime")

  object Versions {
    val finagle  = "6.4.0"
    val spray    = "1.0-M2"
  }

  val finagleCore = "com.twitter"       %% "finagle-core"    % Versions.finagle
  val finagleHttp = "com.twitter"       %% "finagle-http"    % Versions.finagle

  val sprayJson   = "cc.spray"          %% "spray-json"      % "1.1.1"

  val logback     = "ch.qos.logback"    %  "logback-classic" % "1.0.0"
  val slf4j       = "org.slf4j"         %  "slf4j-api"       % "1.6.4"

  val scalatest   = "org.scalatest"     %% "scalatest"       % "1.6.1"
}
