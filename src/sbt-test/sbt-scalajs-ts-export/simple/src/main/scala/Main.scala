package simple

import ch.wavein.typescript._

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/**
  * A simple class and objects to write tests against.
  */

@TSExport
@JSExportAll
@JSExportTopLevel("Api")
object ApiSJS{

  @TSExport
  def test(s:String):String = "test"

  @TSExport
  val test2:Double = 1.0

}

@TSExport
case class Test(a:Int,b:String)

@TSExport
case class Test2(t:Test)

