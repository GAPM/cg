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

import sron.cg.compiler.ast.Location
import sron.cg.compiler.lang.Type

/**
 * Contains all types a symbol can be
 */
enum class SymType {
    VAR,
    FUNC
}

enum class VarKind {
    GLOBAL,
    LOCAL,
    PARAMETER
}

/**
 * Abstract class of all the symbols in the symbol table
 */
abstract class Symbol(val id: String, val symType: SymType, val scope: Scope, val location: Location)

/**
 * Represents a variable in the symbol table
 */
class Variable(id: String, val type: Type, val kind: VarKind, scope: Scope,
               location: Location) : Symbol(id, SymType.VAR, scope, location)

/**
 * Represents a function in the symbol table
 */
class Function(id: String, val type: Type, val params: List<Variable>,
               scope: Scope, location: Location) :
        Symbol(id, SymType.FUNC, scope, location) {

    val signature = Signature(params.map { it.type })
}
