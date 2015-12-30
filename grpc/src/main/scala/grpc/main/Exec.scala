package grpc
package main

import java.io.IOException

import grpc.lib.compiler.Compiler
import grpc.lib.exception.CompilerException
import org.apache.commons.cli.{DefaultParser, Options}

object Exec extends App {
  val options = new Options
  options.addOption("c", false, "Just generate code")

  val parser = new DefaultParser
  val cmd = parser.parse(options, args.asInstanceOf[Array[String]])

  if (cmd.getArgs.isEmpty) {
    println("ERROR: no input files")
    System.exit(1)
  }

  val fileName = cmd.getArgs()(0)
  var compiler = Option.empty[Compiler]

  try {
    compiler = Some(new Compiler(fileName))
  } catch {
    case e: IOException =>
      println(s"ERROR: can't open file `$fileName`")
      System.exit(1)
  }

  compiler match {
    case Some(c) =>
      try {
        c.compile()
      } catch {
        case e: CompilerException => println(e.getMessage)
      }
    case None =>
      println("ERROR: Compiler could not be instantiated")
      System.exit(1)
  }
}
