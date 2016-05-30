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

import sron.cg.compiler.type.Type
import sron.cg.compiler.type.descriptor

/**
 * Represents a function in the symbol table
 */
class Function(name: String, scope: String, val type: Type, location: Location,
               vararg val args: Variable) : Symbol(name, scope, location) {
    override val symType: SymType = SymType.FUNC
    var isSpecial = false

    /**
     * Retrieves the JVM signature for the function
     *
     * @return The signature as a String
     */
    fun signatureString(): String {
        var result = "("
        for (arg in args) {
            result += arg.type.descriptor()
        }
        result += ")"
        result += type.descriptor()
        return result
    }
}