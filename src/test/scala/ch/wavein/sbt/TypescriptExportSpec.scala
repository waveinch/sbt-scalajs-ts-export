package ch.wavein.sbt

import org.scalatest._

import scala.meta._

class TypescriptExportSpec extends FlatSpec with Matchers {
  "Object" should "be exported in typescript" in {
    val program =
      """
        |@TSExport
        |object Api{
        |
        | @TSExport
        | def test(s:String):String = "test"
        |
        | @TSExport
        | val test2:Double = "test2"
        |
        |}""".stripMargin
    val tree = program.parse[Source].get

    TypescriptExport(Seq(tree)) shouldBe """
        |export interface Api{
        |  test(s:string):string;
        |  test2:number;
        |}
      """.stripMargin.trim
  }

  "Case class" should "be exported in typescript" in {
    val program =
      """
        |@TSExport
        |case class Test(a:Int,b:String)
        |""".stripMargin
    val tree = program.parse[Source].get

    TypescriptExport(Seq(tree)) shouldBe """
                                           |export interface Test{
                                           |  a:number;
                                           |  b:string;
                                           |}
                                         """.stripMargin.trim
  }

  it should "be exported when complex types are used" in {
    val program =
      """
        |@TSExport
        |case class Test(a:Int,b:String)
        |
        |@TSExport
        |case class Test2(t:Test)
        |""".stripMargin
    val tree = program.parse[Source].get

    TypescriptExport(Seq(tree)) shouldBe """
                                           |export interface Test{
                                           |  a:number;
                                           |  b:string;
                                           |}
                                           |
                                           |export interface Test2{
                                           |  t:Test;
                                           |}
                                         """.stripMargin.trim
  }

  it should "be exported when optional values are used" in {
    val program =
      """
        |@TSExport
        |case class Test(a:Optional[String])
        |""".stripMargin
    val tree = program.parse[Source].get

    TypescriptExport(Seq(tree)) shouldBe """
                                           |export interface Test{
                                           |  a?:string;
                                           |}
                                         """.stripMargin.trim
  }

  it should "be exported when seq are used" in {
    val program =
      """
        |@TSExport
        |case class Test(a:Seq[String])
        |""".stripMargin
    val tree = program.parse[Source].get

    TypescriptExport(Seq(tree)) shouldBe """
                                           |export interface Test{
                                           |  a:Array<string>;
                                           |}
                                         """.stripMargin.trim
  }

  "Real case" should "be exported" in {
    val program =
      """
        |package test
        |
        |import ch.wavein.typescript.TSExport
        |
        |import scala.concurrent.Future
        |import scala.scalajs.js.annotation._
        |
        |
        |
        |@JSExportTopLevel("Configuration")
        |@JSExportAll
        |@TSExport
        |object Configuration {
        |    @TSExport
        |    def getSimple():String = "config"
        |    @TSExport
        |    def getSimple2():String = "config"
        |}
      """.stripMargin

      val tree = program.parse[Source].get

      println(TypescriptExport(Seq(tree)))

      TypescriptExport(Seq(tree)) shouldBe """
                                             |export interface Configuration{
                                             |  getSimple():string;
                                             |  getSimple2():string;
                                             |}
                                           """.stripMargin.trim


  }
}
