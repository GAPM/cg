package grpc.lib.symbol

abstract class Symbol(val name: String, val scope: String,
                      val location: Location) {
    abstract fun getSymType(): SymType
}