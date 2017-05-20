package sbtgui

import sbt._
import sbt.Keys._

object GuiPlugin extends AutoPlugin {

  object autoImport {
    val nodeModulesDirectory    = settingKey[File]("Location of node_modules directory")
    val nodeModulesBinDirectory = settingKey[File]("Location of installed, executable npm packages")

    val coffeeSourceDirectory = settingKey[File]("Location of CoffeeScript sources")
    val coffeeSourceManifest  = settingKey[Seq[File]]("Ordered set of CoffeeScript sources that will be compiled")
    val jadeSourceDirectory   = settingKey[File]("Location of Jade templates")
    val stylusSourceDirectory = settingKey[File]("Location of Stylus sources")
    val cssTargetDirectory    = settingKey[File]("Location of generated CSS resources")
    val jsTargetDirectory     = settingKey[File]("Location of generated JavaScript resources")
  }

  import autoImport._

  override def requires = plugins.JvmPlugin

  override def projectSettings = Seq(
    nodeModulesDirectory := baseDirectory.value / "node_modules",
    nodeModulesBinDirectory := nodeModulesDirectory.value / ".bin",

    coffeeSourceDirectory := (sourceDirectory in Compile).value / "coffee",
    stylusSourceDirectory := (sourceDirectory in Compile).value / "styl",
    jadeSourceDirectory   := (sourceDirectory in Compile).value / "jade",
    jsTargetDirectory     := (resourceManaged in Compile).value / "assets" / "scripts",
    cssTargetDirectory    := (resourceManaged in Compile).value / "assets" / "styles",

    coffeeSourceManifest := Seq(
      "http.coffee",
      "main.coffee",
      "models.coffee",
      "forms.coffee",
      "lineup.coffee",
      "player.coffee",
      "spec.coffee",
      "step1.coffee",
      "step2.coffee",
      "step3.coffee"
    ).map(coffeeSourceDirectory.value / _),

    resourceGenerators in Compile ++= Seq(
      // generate js from coffeescript sources
      Def.task {
        val log = streams.value.log
        log.info(s"Compiling CoffeeScript sources to JavaScript")
        val target = jsTargetDirectory.value / "main.js"
        val bin = nodeModulesBinDirectory.value
        val output = Process(s"$bin/coffee -c -p -j noop " + coffeeSourceManifest.value.mkString(" ")) !! log
        IO.write(target, output)
        Seq(target)
      }.taskValue,
      // compile jade templates to js
      Def.task {
        val log = streams.value.log
        log.info(s"Compiling Jade templates to JavaScript")
        val target = jsTargetDirectory.value / "templates.js"
        val bin = nodeModulesBinDirectory.value
        val buffer = Seq.newBuilder[String]
        buffer += "LineupGui = {};"
        buffer += "LineupGui.templates = {};"
        (jadeSourceDirectory.value * "*.jade")
          .get
          .foreach { template =>
            log.info(s"Compiling Jade template $template to client-side JavaScript code")
            val (name, _) = IO.split(template.getName)
            val javascript = Process(s"$bin/jade -c -D") #< template !! log
            buffer += s"LineupGui.templates['$name'] = (function(){return $javascript;})();"
          }
        IO.writeLines(target, buffer.result())
        Seq(target)
      }.taskValue,
      // generate css from stylus sources
      Def.task {
        val log = streams.value.log
        val src = stylusSourceDirectory.value
        val target = cssTargetDirectory.value
        val bin = nodeModulesBinDirectory.value
        val nodeModules = nodeModulesDirectory.value
        log.info(s"Generating CSS from Stylus source files")
        IO.createDirectory(target)
        Process(s"$bin/stylus -c -u ${nodeModules / "nib"} -o $target $src") ! log
        target.***.get
      }.taskValue
    )
  )
}
