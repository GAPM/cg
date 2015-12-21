package grpc.lib.symbol

import scala.collection.mutable.ListBuffer

class Function(val name: String, val retType: Type.Value,
               val location: Location, val args: ListBuffer[Variable])
  extends Symbol(name, "global", location) {

  override def getSymType = SymType.FUNC
}
