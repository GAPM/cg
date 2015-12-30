package grpc
package lib
package symbol

class Variable(val name: String, val typ: Type.Value, val scope: String,
               val location: Location)
  extends Symbol(name, scope, location) {

  override def getSymType = SymType.VAR
}
