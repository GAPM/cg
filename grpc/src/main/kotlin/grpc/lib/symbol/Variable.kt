package grpc.lib.symbol

class Variable(name: String, val type: Type, scope: String,
               location: Location) : Symbol(name, scope, location) {

    override fun getSymType(): SymType = SymType.VAR
}
