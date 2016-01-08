package grpc.lib.compiler.phase

import grpc.lib.compiler.UnitResult
import grpc.lib.compiler.internal.GrpBaseListener
import grpc.lib.symbol.Location
import grpc.lib.symbol.SymbolTable
import org.antlr.v4.runtime.tree.ParseTreeProperty
import java.util.*

open class Phase : GrpBaseListener() {
    var symTab: SymbolTable? = null
    var fileName: String? = null
    var results: ParseTreeProperty<UnitResult>? = null
    val errorList = ArrayList<String>()

    fun addError(location: Location, msg: String) =
            errorList.add("$fileName:$location: $msg")
}
