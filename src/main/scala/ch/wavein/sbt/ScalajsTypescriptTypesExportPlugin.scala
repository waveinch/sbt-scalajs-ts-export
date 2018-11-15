package ch.wavein.sbt

import org.scalajs.sbtplugin.ScalaJSPlugin
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt._
import sbt.Keys._

import scala.meta._
import scala.meta.internal.parsers.ScalametaParser
import scala.meta.internal.tokenizers.PlatformTokenizerCache

object ScalajsTypescriptTypesExportPlugin extends AutoPlugin {

  private val pluginVersion = "0.1-SNAPSHOT"

  override def trigger = allRequirements
  override def requires = ScalaJSPlugin

  object autoImport {
    val outputDir = settingKey[File]("Path where to put the typescript definition file")
    val generateTypescript = taskKey[File]("Generate typescript file")
    val generatePackage = taskKey[File]("Generate package.json file")
    val jsOutputName = settingKey[String]("Output js filename without extention")
    val packageTs = taskKey[Unit]("Package all")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    artifactPath in fastOptJS in Compile := outputDir.value / (jsOutputName.value + ".js"),
    artifactPath in fullOptJS in Compile := outputDir.value / (jsOutputName.value + ".js"),
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) },
    scalaJSUseMainModuleInitializer := false,
    libraryDependencies ++= Seq("ch.wavein" %% """scalajs-ts-export""" % pluginVersion),
    outputDir := (baseDirectory in Compile).value / "target" / "web" / "js",
    (crossTarget in fullOptJS) := outputDir.value,
    (crossTarget in fastOptJS) := outputDir.value,
    jsOutputName := name.value,
    generateTypescript := {
      PlatformTokenizerCache.megaCache.clear()
      val outputFile = outputDir.value / (jsOutputName.value + ".d.ts")
      val sources:Seq[Source] = (unmanagedSources in Compile).value.map { file =>
        // Workaround for https://github.com/scalameta/scalameta/issues/874
        new ScalametaParser(Input.File(file), dialects.ParadiseTypelevel212).parseSource()
      }

      val content = TypescriptExport(sources)

      IO.write(outputFile, content, scala.io.Codec.UTF8.charSet)
      outputFile
    },
    generatePackage := {
      val outputFile = outputDir.value / "package.json"
      val content =
        s"""
          |{
          |  "name": "${name.value}",
          |  "main": "${jsOutputName.value}.js",
          |  "version": "${version.value}"
          |}
        """.stripMargin

      IO.write(outputFile, content, scala.io.Codec.UTF8.charSet)
      outputFile
    },
    packageTs := Def.sequential(
      (fullOptJS in Compile),
      generatePackage,
      generateTypescript
    ).value
  )

  override lazy val buildSettings = Seq()

  override lazy val globalSettings = Seq()
}
