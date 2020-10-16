

val baseName = "scalajs-ts-export"

lazy val commonSettings = Seq(
  organization := "ch.wavein",
  version := "0.4",
  bintrayPackageLabels := Seq("sbt","plugin"),
  bintrayVcsUrl := Some("""git@github.com:waveinch/sbt-scalajs-ts-export.git"""),
  bintrayOrganization := Some("waveinch"),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)





lazy val root = project.in(file("."))
  .enablePlugins(ScriptedPlugin)
  .dependsOn(extra)
  .aggregate(extra)
  .settings(commonSettings: _*)
  .settings(
    name := s"sbt-$baseName",
    sbtPlugin := true,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test",
    libraryDependencies += "org.scalameta" %% "scalameta" % "4.3.24",
    initialCommands in console := """import ch.wavein.sbt._""",
    // set up 'scripted; sbt plugin for testing sbt plugins
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.3.0")
  )

lazy val extra = project.in(file("extra"))
  .settings(commonSettings: _*)
  .settings(
    name := baseName
  )
