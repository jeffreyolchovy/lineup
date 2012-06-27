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
  def container (deps: ModuleID*): Seq[ModuleID] = deps map (_ % "container")

  object V {
    val akka     = "2.0"
    val spray    = "1.0-M2"
  }

  val akkaActor   = "com.typesafe.akka" %  "akka-actor"      % V.akka
  val akkaSlf4j   = "com.typesafe.akka" %  "akka-slf4j"      % V.akka

  val sprayServer = "cc.spray"          %  "spray-server"    % V.spray
  val sprayJson   = "cc.spray"          %% "spray-json"      % "1.1.1"

  val jettyWebApp = "org.eclipse.jetty" %  "jetty-webapp"    % "8.1.0.v20120127"
  val logback     = "ch.qos.logback"    %  "logback-classic" % "1.0.0"
  val slf4j       = "org.slf4j"         %  "slf4j-api"       % "1.6.4"

  val specs2      = "org.specs2"        %% "specs2"          % "1.7.1"
  val scalatest   = "org.scalatest"     %% "scalatest"       % "1.6.1"
}
