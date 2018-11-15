package simple

import ch.wavein.typescript._

/**
  * A simple class and objects to write tests against.
  */

@TSExport
object Api{

  @TSExport
  def test(s:String):String = "test"

  @TSExport
  val test2:Double = "test2"

}

@TSExport
case class Test(a:Int,b:String)

@TSExport
case class Test2(t:Test)