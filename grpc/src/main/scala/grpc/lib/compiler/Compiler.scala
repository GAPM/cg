package grpc.lib.compiler

import java.io.{File, FileInputStream}

import grpc.lib.exception.{ErrorsInCodeException, ParsingException}
import grpc.lib.internal.{GrpLexer, GrpParser}
import grpc.lib.symbol.SymbolTable
import org.antlr.v4.runtime.tree.{ParseTree, ParseTreeProperty, ParseTreeWalker}
import org.antlr.v4.runtime.{ANTLRInputStream, CommonTokenStream}

class Compiler(path: String) {
  private val file = new File(path)
  private val is = new FileInputStream(file)
  private val input = new ANTLRInputStream(is)
  private val lexer = new GrpLexer(input)
  private val tokens = new CommonTokenStream(lexer)
  private val parser = new GrpParser(tokens)
  private var totalErrors = 0

  private val symTab = new SymbolTable
  private val results = new ParseTreeProperty[UnitResult]

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

  private def executePhase(tree: ParseTree, compilerPhase: Class[_]) {
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
          totalErrors += phase.errorCount()
          for (e <- phase.getErrorList) println(e)
        }
      case None =>
    }
  }

  def compile() {
    val tree = parser.init()
    checkParsing()

    executePhase(tree, classOf[StructureCheck])
    executePhase(tree, classOf[GlobalDeclarations])
    executePhase(tree, classOf[TypeCheck])

    checkForErrors()
  }
}
