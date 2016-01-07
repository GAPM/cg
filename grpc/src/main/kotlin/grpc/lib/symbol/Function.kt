package grpc.lib.symbol

import java.util.*


class Function(name: String, val type: Type, location: Location,
               val args: ArrayList<Variable>) :
        Symbol(name, "global", location) {

    override fun getSymType(): SymType = SymType.FUNC
}