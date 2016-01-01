package grpc
package lib
package symbol

import grpc.lib.symbol.Type.Type

class Variable(val name: String, private val typ: Type, val scope: String,
               val location: Location)
  extends Symbol(name, scope, location) {

  def getType: Type = typ

  override def getSymType = SymType.VAR
}
