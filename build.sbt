inThisBuild(
  Seq(
    organization  := "com.olchovy",
    version       := "0.2.0-SNAPSHOT",
    scalaVersion  := "2.12.2",
    scalacOptions := Seq("-deprecation", "-feature", "-language:_")
  )
)

cancelable in Global := true

lazy val root = (project in file(".")).aggregate(ga, common, api, gui)

val ga = (project in file("ga"))
  .settings(
    libraryDependencies ++= Seq(
      "org.slf4j" % "slf4j-api" % "1.7.25",
      "org.scalatest" %% "scalatest"  % "3.0.1" % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Test
    )
  )

val common = (project in file("common"))
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.6",
      "org.json4s" %% "json4s-native" % "3.5.2",
      "com.twitter" %% "finagle-core" % "6.44.0",
      "com.twitter" %% "finagle-http" % "6.44.0",
      "org.scalatest" %% "scalatest"  % "3.0.1" % Test,
      "ch.qos.logback" % "logback-classic" % "1.2.3" % Test
    )
  )
  .dependsOn(ga)

val api = (project in file("api"))
  .settings(
    libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
    mainClass in (Compile, run) := Some("com.olchovy.lineup.ApiServer")
  )
  .dependsOn(common)

val gui = (project in file("gui"))
  .settings(
    libraryDependencies += "com.lihaoyi" %% "scalatags" % "0.6.5",
    mainClass in (Compile, run) := Some("com.olchovy.lineup.WebAppServer")
  )
  .dependsOn(api)
  .enablePlugins(GuiPlugin)
