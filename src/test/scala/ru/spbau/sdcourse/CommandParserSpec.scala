package ru.spbau.sdcourse

import org.scalatest._
import scala.collection.JavaConverters._
/**
  * Created by svloyso on 21.09.16.
  */
class CommandParserSpec extends FlatSpec with Matchers {
  "A CommandParser" should "parse simple command with arguments" in {
    val testCommand = "cmd arg1 arg2 arg3"
    val parsedCommand = CommandParser.parse(testCommand)
    parsedCommand shouldEqual List(Cmd("cmd", List("arg1", "arg2", "arg3").asJava)).asJava
  }
  it should "parse command connected pipelines" in {
    val testPipe = "cmd1 arg1 arg2 | cmd2 arg1 arg2"
    val parsedPipe = CommandParser.parse(testPipe)
    parsedPipe shouldEqual List(Cmd("cmd1", List("arg1", "arg2").asJava), Cmd("cmd2", List("arg1", "arg2").asJava)).asJava
  }
  it should "parse assignment" in {
    val testAssignment = "KEY=value"
    val parsedAssignment = CommandParser.parse(testAssignment)
    parsedAssignment shouldEqual List(Cmd("$assignment", List("KEY", "value").asJava)).asJava
  }
  it should "parse quotes" in {
    val testQuotes = "cmd \"continues ' arg\" arg 'continues \" arg'"
    val parsedQuotes = CommandParser.parse(testQuotes)
    parsedQuotes shouldEqual List(Cmd("cmd", List("continues ' arg", "arg", "continues \" arg").asJava)).asJava
  }
}
