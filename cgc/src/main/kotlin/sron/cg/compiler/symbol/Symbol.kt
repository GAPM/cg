/*
 * Copyright 2016 Simón Oroño & La Universidad del Zulia
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

package sron.cg.compiler.symbol

import sron.cg.compiler.lang.Type

/**
 * Contains all types a symbol can be
 */
enum class SymType {
    VAR,
    FUNC
}

/**
 * Abstract class of all the symbols in the symbol table
 */
abstract class Symbol(val id: String, val symType: SymType, val scope: Scope)

/**
 * Represents a variable in the symbol table
 */
class Variable(id: String, val type: Type, scope: Scope) :
        Symbol(id, SymType.VAR, scope)

/**
 * Represents a function in the symbol table
 */
class Function(id: String, val type: Type, scope: Scope,
               val params: List<Variable>) : Symbol(id, SymType.FUNC, scope) {

    /**
     * Retrieves the JVM signature for the function
     *
     * @return The signature as a String
     */
    fun signatureString(): String {
        val sig = params.map {
            it.type.descriptor()
        }.joinToString(separator = "")
        return "($sig)${type.descriptor()}"
    }
}
