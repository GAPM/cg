package grpc.lib.symbol

import scala.collection.mutable.ListBuffer

class SymbolTable {
  val symTab = ListBuffer.empty[Symbol]

  /**
    * Gets a symbol with matching name, scope and type.
    *
    * @param name  the name to be matched
    * @param scope the scope to be matched
    * @param typ  the type to be matched
    * @return a symbol with matching name, scope and type if its found
    */
  def getSymbol(name: String, scope: String, typ: SymType.Value): Option[Symbol] = {
    symTab.find(p =>
      p.getName == name &&
        (p.getScope == scope || scope == "") &&
        p.getSymType == typ
    )
  }

  /**
    * Gets a symbol with matching name, scope and type.
    *
    * @param name  the name to be matched
    * @param typ  the type to be matched
    * @return a symbol with matching name, scope and type if its found
    */
  def getSymbol(name: String, typ: SymType.Value): Option[Symbol] =
    getSymbol(name, "", typ)

  def addSymbol(s: Symbol) = symTab += s
}
