package grpc
package lib
package compiler
package phase

import grpc.lib.compiler.internal.GrpBaseListener
import grpc.lib.exception.FatalCompilationErrorException
import grpc.lib.symbol.{Location, SymbolTable}
import org.antlr.v4.runtime.tree.ParseTreeProperty

import scala.collection.mutable.ListBuffer

class Phase extends GrpBaseListener {
  protected var symbolTable: SymbolTable = null
  protected var fileName: String = null
  protected var results: ParseTreeProperty[UnitResult] = null
  private val errorList = ListBuffer.empty[String]

  def setSymbolTable(symbolTable: SymbolTable) {
    this.symbolTable = symbolTable
  }

  def setFileName(fileName: String) {
    this.fileName = fileName
  }

  def setResults(results: ParseTreeProperty[UnitResult]) {
    this.results = results
  }

  def addError(location: Location, msg: String) {
    errorList += s"$fileName:$location: error: $msg".toString
  }

  def fatalError(location: Location, msg: String) {
    throw new FatalCompilationErrorException(s"$fileName:$location: error: $msg".toString)
  }

  def errorCount(): Int = errorList.size

  def getErrorList: ListBuffer[String] = errorList
}
