package grpc.lib.compiler

import java.io.{File, FileInputStream}

import grpc.lib.exception.ParsingException
import grpc.lib.internal.{GrpLexer, GrpParser}
import grpc.lib.symbol.SymbolTable
import org.antlr.v4.runtime.tree.{ParseTreeProperty, ParseTreeWalker}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

class Compiler(path: String) {
  val file = new File(path)
  val is = new FileInputStream(file)
  val input = new ANTLRInputStream(is)
  val lexer = new GrpLexer(input)
  val tokens = new CommonTokenStream(lexer)
  val parser = new GrpParser(tokens)

  val tree = parser.init()
  val symTab = new SymbolTable
  val results = new ParseTreeProperty[UnitResult]

  def checkParsing() = {
    if (parser.getNumberOfSyntaxErrors > 0) {
      throw new ParsingException
    }
  }

  private def executePhase(compilerPhase: Class[_]) = {
    val walker = new ParseTreeWalker
    var listener = Option.empty[CompilerPhase]

    try {
      listener = Some(compilerPhase.newInstance().asInstanceOf[CompilerPhase])
    } catch {
      case _: ReflectiveOperationException | _: ClassCastException =>
        println("ERROR: (Internal) Ill formed phase class")
    }

    listener match {
      case Some(phase) =>
        phase.setSymbolTable(symTab)
        phase.setResults(results)
        phase.setFileName(file.getName)

        walker.walk(phase, tree)

        if (phase.errorCount() > 0) {
          for (e <- phase.getErrorList) println(e)
        }
      case None =>
    }
  }

  def compile() = {
    checkParsing()
    executePhase(classOf[StructureCheck])
    executePhase(classOf[GlobalDeclarations])
    executePhase(classOf[TypeCheck])
  }
}
