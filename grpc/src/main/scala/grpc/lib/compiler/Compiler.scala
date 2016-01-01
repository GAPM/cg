package grpc
package lib
package compiler

import java.io.{File, FileInputStream, InputStreamReader}
import java.nio.charset.Charset

import grpc.lib.compiler.internal.{GrpLexer, GrpParser}
import grpc.lib.compiler.phase.{Globals, Phase, Structure, Types}
import grpc.lib.exception.{ErrorsInCodeException, ParsingException}
import grpc.lib.symbol.SymbolTable
import grpc.lib.util.Logger
import grpc.lib.util.Logger.LogLevel
import org.antlr.v4.runtime.tree.{ParseTree, ParseTreeProperty, ParseTreeWalker}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

class Compiler(path: String, debug: Boolean) {
  private val file = new File(path)
  private val is = new FileInputStream(file)
  private val reader = new InputStreamReader(is, Charset.defaultCharset())
  private val input = new ANTLRInputStream(reader)
  private val lexer = new GrpLexer(input)
  private val tokens = new CommonTokenStream(lexer)
  private val parser = new GrpParser(tokens)
  private var totalErrors = 0

  private val symTab = new SymbolTable
  private val results = new ParseTreeProperty[UnitResult]

  if (debug) {
    Logger.setMaxLevel(LogLevel.DEBUG)
  }

  private def checkParsing() {
    if (parser.getNumberOfSyntaxErrors > 0) {
      throw new ParsingException
    }
  }

  private def checkForErrors() {
    if (totalErrors > 0) {
      throw new ErrorsInCodeException(totalErrors)
    }
  }

  private def executePhase(tree: ParseTree, phase: Phase) {
    val walker = new ParseTreeWalker

    phase.setSymbolTable(symTab)
    phase.setResults(results)
    phase.setFileName(file.getName)

    val start = System.currentTimeMillis()
    walker.walk(phase, tree)
    val end = System.currentTimeMillis()

    if (phase.errorCount() > 0) {
      totalErrors += phase.errorCount()
      phase.getErrorList foreach { e => Logger.log(e, LogLevel.ERROR) }
    }

    Logger.log(s"Phase ${phase.getClass.getName}: " +
      s"${end - start} millis".toString, LogLevel.DEBUG)
  }

  def compile() {
    val tree = parser.init()
    checkParsing()

    executePhase(tree, new Structure)
    executePhase(tree, new Globals)
    executePhase(tree, new Types)

    checkForErrors()
  }
}
