package grpc.lib.symbol

/**
  * Symbol class from which all other symbols in the symbol table inherit
  */
abstract class Symbol(private val name: String, private val scope: String,
                      private val location: Location) {
  def getName = name

  def getScope = scope

  def getLocation = location

  def getSymType: SymType.Value
}
