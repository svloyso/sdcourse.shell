package ru.spbau.sdcourse

import scala.util.parsing.combinator._
import scala.collection.JavaConverters._

sealed trait Expression
case class Quoted(text: String) extends Expression
case class Cmd(name: String, args: java.util.List[String]) extends Expression
case class Pipe(commands: List[Cmd]) extends Expression

/**
  * Simple command parser. Supports quotes and pipes.
  * Returns java list with Cmd entities.
  */
object ShParser extends RegexParsers {
  def token:      Parser[String]     = "[^ |=\"']+".r
  def quoted:     Parser[Expression] = ("\"" ~ "[^\"]*".r ~ "\"") ^^ { case _ ~ text ~ _ => Quoted(text) } | ("'" ~ raw"[^']*".r ~ "'") ^^ { case _ ~ text ~ _ => Quoted(text) }
  def assignment: Parser[Cmd] = token ~ "=" ~ (token | quoted) ^^ {
    case variable ~ "=" ~ Quoted(value)  => Cmd("$assignment", List(variable, value).asJava)
    case variable ~ "=" ~ (value:String) => Cmd("$assignment", List(variable, value).asJava)
  }
  def command:    Parser[Cmd] = assignment | token ~ rep(token | quoted) ^^ {
    case cmd ~ args => Cmd(cmd, args.map {
      case Quoted(arg) => arg
      case arg:String  => arg
    } asJava)
  }
  def pipe:      Parser[Pipe] = (command ~ "|" ~ pipe) ^^ { case cmd ~ "|" ~ Pipe(cmds) => Pipe(cmd :: cmds) } | command ^^ { case cmd => Pipe(List(cmd)) }
}

object CommandParser {
  def parse(text: String): java.util.List[Cmd] = {
    val res = ShParser.parseAll(ShParser.pipe, text)
    res match {
      case ShParser.Success(r, _) => r.commands.asJava
      case err => null
    }
  }
}
