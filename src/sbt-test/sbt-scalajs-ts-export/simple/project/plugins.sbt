{
  val pluginVersion =
  if(System.getProperty("plugin.version") == null) {
    "0.3"
  } else {
    System.getProperty("plugin.version")
  }

  addSbtPlugin("ch.wavein" % """sbt-scalajs-ts-export""" % pluginVersion)
}
