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

  it should "support boolean" in {
    val program =
      """
        |@TSExport
        |case class Test(a:Boolean,b:String)
        |""".stripMargin
    val tree = program.parse[Source].get

    TypescriptExport(Seq(tree)) shouldBe """
                                           |export interface Test{
                                           |  a:boolean;
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

  it should "support promises and complex types" in {
    val program =
      """
        |package com.teamdatalog.client.core.api
        |
        |import ch.wavein.typescript.TSExport
        |
        |import scala.scalajs.js.Promise
        |import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}
        |
        |@JSExportTopLevel("Configuration")
        |@JSExportAll
        |@TSExport
        |object Configuration {
        |
        |  @TSExport
        |  def fetch():Promise[ConfigurationVM] = ???
        |
        |}
        |
        |@TSExport
        |case class ConfigurationVM(title:String)
        |
      """.stripMargin

      val tree = program.parse[Source].get

      println(TypescriptExport(Seq(tree)))

      TypescriptExport(Seq(tree)) shouldBe """
                                             |export interface Configuration{
                                             |  fetch():Promise<ConfigurationVM>;
                                             |}
                                             |
                                             |export interface ConfigurationVM{
                                             |  title:string;
                                             |}
                                           """.stripMargin.trim
  }



  "Lambda function" should "be valid" in {
    val program =
      """
        |@TSExport
        |object Test {
        |
        |  @TSExport
        |  def bind(getItemJs:String => String) {
        |  }
        |}
      """.stripMargin

    val tree = program.parse[Source].get

    println(TypescriptExport(Seq(tree)))

    TypescriptExport(Seq(tree)) shouldBe """
                                           |export interface Test{
                                           |  bind(getItemJs:(x0:string) => string):any;
                                           |}
                                         """.stripMargin.trim
  }

  it should "trasform promise functions" in {

    val program =
      """
        |@TSExport
        |object Test {
        |
        |  @TSExport
        |  def bind(getItemJs:String => js.Promise[String]) {
        |  }
        |}
      """.stripMargin

    val tree = program.parse[Source].get

    println(TypescriptExport(Seq(tree)))

    TypescriptExport(Seq(tree)) shouldBe """
                                           |export interface Test{
                                           |  bind(getItemJs:(x0:string) => Promise<string>):any;
                                           |}
                                         """.stripMargin.trim


  }

  it should "trasform promise custom type functions" in {

    val program =
      """
        |@TSExport
        |object Test {
        |
        |  @TSExport
        |  def bind(getItemJs:Conf => js.Promise[Conf]) {
        |  }
        |}
        |
        |@TSExport
        |case class Conf(title:String)
      """.stripMargin

    val tree = program.parse[Source].get

    println(TypescriptExport(Seq(tree)))

    TypescriptExport(Seq(tree)) shouldBe
      """
        |export interface Test{
        |  bind(getItemJs:(x0:Conf) => Promise<Conf>):any;
        |}
        |
        |export interface Conf{
        |  title:string;
        |}
      """.stripMargin.trim
  }

  it should "export a real case" in {
      val program =
        """
          |
          |
          |
          |@TSExport
          |object Init {
          |
          |  @TSExport
          |  def bind(
          |    getItemJs:String => js.Promise[String],
          |    setItemJs:(String,String) => js.Promise[Unit],
          |    removeItemJs: String => js.Promise[Unit]
          |           ): Unit = {
          |    ???
          |  }
          |}
          |
          |
          |
        """.stripMargin

      val tree = program.parse[Source].get

      println(TypescriptExport(Seq(tree)))

      TypescriptExport(Seq(tree)) shouldBe """
                                             |export interface Init{
                                             |  bind(getItemJs:(x0:string) => Promise<string>,setItemJs:(x0:string,x1:string) => Promise<any>,removeItemJs:(x0:string) => Promise<any>):any;
                                             |}
                                           """.stripMargin.trim
    }



}
