package grpc.lib.symbol

import java.util.*

class SymbolTable {
    private val symTab = ArrayList<Symbol>()

    /**
     * Gets a symbol with matching name, scope and type.
     *
     * @param name  the name to be matched
     * @param scope the scope to be matched
     * @param symType  the type to be matched
     * @return a symbol with matching name, scope and type if its found
     */
    fun getSymbol(name: String, scope: String, symType: SymType): Symbol? {
        return symTab.find {
            it.name == name && (it.scope == scope || scope == "") &&
                    it.getSymType() == symType
        }
    }

    /**
     * Gets a symbol with matching name and type.
     *
     * @param name  the name to be matched
     * @param symType  the type to be matched
     * @return a symbol with matching name, scope and type if its found
     */
    fun getSymbol(name: String, symType: SymType): Symbol? {
        return getSymbol(name, "", symType)
    }

    fun addSymbol(s: Symbol) = symTab.add(s)
}