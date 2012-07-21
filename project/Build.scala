import sbt._
import com.github.siasia._
import WebPlugin._
import PluginKeys._
import Keys._


object Build extends sbt.Build
{
  import Dependencies._

  lazy val myProject = Project("lineup", file("."))
    .settings(WebPlugin.webSettings: _*)
    .settings(port in config("container") := 8080)
    .settings(
      organization  := "com.olchovy",
      version       := "0.1.0",
      scalaVersion  := "2.9.1",
      scalacOptions := Seq("-deprecation", "-encoding", "utf8"),
      resolvers     ++= Dependencies.resolutionRepos,
      libraryDependencies ++=
        compile(akkaActor, akkaSlf4j, sprayServer, sprayJson) ++
        test(specs2, scalatest) ++
        runtime(slf4j, logback) ++
        container(jettyWebApp, slf4j, logback)
    )
}

