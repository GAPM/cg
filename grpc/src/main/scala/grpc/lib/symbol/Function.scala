package grpc
package lib
package symbol

import grpc.lib.symbol.Type.Type

import scala.collection.mutable.ListBuffer

class Function(val name: String, private val retType: Type,
               val location: Location, private val args: ListBuffer[Variable])
  extends Symbol(name, "global", location) {

  def getArgs: ListBuffer[Variable] = args

  def getRetType: Type.Value = retType

  override def getSymType = SymType.FUNC
}
