package grpc
package main

import java.io.IOException

import grpc.lib.compiler.Compiler
import grpc.lib.exception.CompilerException
import grpc.lib.util.Logger
import grpc.lib.util.Logger.LogLevel
import org.apache.commons.cli._

import scala.Option

/**
  * Compiler entry point
  *
  * TODO: Rework this to reduce complexity
  */
object Exec extends App {
  val options = new Options
  options.addOption("c", false, "Just generate code")
  options.addOption("d", false, "Print compiler debug messages")
  options.addOption("h", "help", false, "Show usage help")

  val parser = new DefaultParser
  var cl = Option.empty[CommandLine]

  try {
    cl = Some(parser.parse(options, args.asInstanceOf[Array[String]]))
  } catch {
    case e: UnrecognizedOptionException =>
      Logger.log(s"Unrecognized option ${e.getOption}".toString, LogLevel.ERROR)
      System.exit(1)
  }

  cl match {
    case Some(cmd) =>
      if (cmd.hasOption("h")) {
        val help = new HelpFormatter
        help.printHelp("grpc", options)
        System.exit(0)
      }

      if (cmd.getArgs.isEmpty) {
        Logger.log("No input files", LogLevel.ERROR)
        System.exit(1)
      }

      val fileName = cmd.getArgs()(0)
      val debug = cmd.hasOption("d")
      var compiler = Option.empty[Compiler]

      try {
        compiler = Some(new Compiler(fileName, debug))
      } catch {
        case e: IOException =>
          Logger.log(s"can't open file `$fileName`".toString, LogLevel.ERROR)
          System.exit(1)
      }

      compiler match {
        case Some(c) =>
          try {
            c.compile()
          } catch {
            case e: CompilerException =>
              Logger.log(e.getMessage, LogLevel.ERROR)
          }
        case None =>
          Logger.log("compiler could not be instantiated", LogLevel.ERROR)
          System.exit(1)
      }
    case None =>
      Logger.log("error while parsing command line arguments", LogLevel.ERROR)
  }
}
