package grpc.lib.compiler

import grpc.lib.internal.GrpParser.TypContext
import grpc.lib.internal.{GrpBaseListener, GrpLexer}
import grpc.lib.symbol.{Location, SymbolTable, Type}
import org.antlr.v4.runtime.tree.{ParseTreeProperty, TerminalNode}

import scala.collection.mutable.ListBuffer

class CompilerPhase extends GrpBaseListener {
  protected var symbolTable: SymbolTable = null
  protected var fileName: String = null
  protected var results: ParseTreeProperty[UnitResult] = null
  private val errorList = ListBuffer.empty[String]

  def getMachineArch: Int = {
    val arch = System.getProperty("os.arch")
    if (arch.endsWith("64")) 64 else 32
  }

  def getType(ctx: TypContext): Type.Value = {
    val tn = ctx.getChild(0).asInstanceOf[TerminalNode]
    val t = tn.getSymbol.getType

    t match {
      case GrpLexer.INT => if (getMachineArch == 64) Type.int64 else Type.int32
      case GrpLexer.INT8 => Type.int8
      case GrpLexer.INT16 => Type.int16
      case GrpLexer.INT32 => Type.int32
      case GrpLexer.INT64 => Type.int64
      case GrpLexer.FLOAT => Type.float
      case GrpLexer.DOUBLE => Type.double
      case GrpLexer.UINT8 => Type.uint8
      case GrpLexer.UINT16 => Type.uint16
      case GrpLexer.UINT32 => Type.uint32
      case GrpLexer.UINT64 => Type.uint64
      case GrpLexer.BOOL => Type.bool
      case GrpLexer.VOID => Type.void
      case GrpLexer.STRING => Type.string
      case GrpLexer.CHAR => Type.char
      case _ => Type.error
    }
  }

  def setSymbolTable(symbolTable: SymbolTable) = {
    this.symbolTable = symbolTable
  }

  def setFileName(fileName: String) = {
    this.fileName = fileName
  }

  def setResults(results: ParseTreeProperty[UnitResult]) = {
    this.results = results
  }

  def addError(location: Location, msg: String) {
    errorList += s"$fileName:$location: error: $msg".toString
  }

  def errorCount(): Int = errorList.size

  def getErrorList: ListBuffer[String] = errorList
}
