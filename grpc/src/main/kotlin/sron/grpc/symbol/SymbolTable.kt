/*
 * Copyright 2016 Simón Oroño
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sron.grpc.symbol

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
    fun getSymbol(name: String, scope: String, symType: SymType): Symbol? =
            symTab.filter { it.name == name }
                    .filter { scope.startsWith(it.scope) || scope == "" }
                    .filter { it.symType == symType }
                    .firstOrNull()

    /**
     * Gets a symbol with matching name and type.
     *
     * @param name  the name to be matched
     * @param symType  the type to be matched
     * @return a symbol with matching name, scope and type if its found
     */
    fun getSymbol(name: String, symType: SymType): Symbol? =
            getSymbol(name, "", symType)

    /**
     * Inserts a symbol into the symbol table
     *
     * @param s The symbol to be added
     */
    fun addSymbol(s: Symbol) = symTab.add(s)
}
