

val baseName = "scalajs-ts-export"

lazy val commonSettings = Seq(
  organization := "ch.wavein",
  version := "0.1-SNAPSHOT",
  bintrayPackageLabels := Seq("sbt","plugin"),
  bintrayVcsUrl := Some("""git@github.com:waveinch/sbt-scalajs-ts-export.git""")
)





lazy val root = project.in(file("."))
  .enablePlugins(ScriptedPlugin)
  .dependsOn(extra)
  .aggregate(extra)
  .settings(commonSettings: _*)
  .settings(
    name := s"sbt-$baseName",
    sbtPlugin := true,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test",
    libraryDependencies += "org.scalameta" %% "scalameta" % "4.0.0",
    initialCommands in console := """import ch.wavein.sbt._""",
    // set up 'scripted; sbt plugin for testing sbt plugins
    scriptedLaunchOpts ++= Seq("-Xmx1024M", "-Dplugin.version=" + version.value),
    addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.25")
  )

lazy val extra = project.in(file("extra"))
  .settings(commonSettings: _*)
  .settings(
    name := baseName
  )