

lazy val root = (project in file(".")).
  settings(
    version := "0.1",
    scalaVersion := "2.12.12",
    name := "test"
    
    
    
  ).enablePlugins(ScalajsTypescriptTypesExportPlugin)
