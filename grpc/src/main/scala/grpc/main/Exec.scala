package grpc.main

import java.io.{File, IOException}

import grpc.lib.compiler.Compiler
import grpc.lib.exception.CompilerException
import org.apache.commons.cli.{DefaultParser, Options}

object Exec extends App {
  def findExec(name: String): Option[String] = {
    val path = System.getenv("PATH")
    val dirs = path.split(File.pathSeparator)

    for (dir <- dirs) {
      val f = new File(dir, name)
      if (f.exists() && !f.isDirectory && f.canExecute) {
        return Some(f.getAbsolutePath)
      }
    }

    None
  }

  val options = new Options
  options.addOption("c", false, "Just generate code")

  val parser = new DefaultParser
  val cmd = parser.parse(options, args.asInstanceOf[Array[String]])

  val exec = findExec("clang")

  if (cmd.hasOption("c") || exec.isEmpty) {
    println("WARNING: Will only generate code")
  }

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
      println(s"ERROR: can't open $fileName")
      System.exit(1)
  }

  compiler match {
    case Some(c) =>
      try {
        c.compile()
      } catch {
        case e: CompilerException =>
          println(e.getMessage)
      }
    case None =>
  }
}
