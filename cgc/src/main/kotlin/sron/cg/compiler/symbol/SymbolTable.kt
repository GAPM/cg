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

import java.util.*

class SymbolTable : LinkedList<Symbol>() {
    /**
     * Gets a symbol with matching name, scope and type.
     *
     * @param name  the name to be matched
     * @param scope the scope to be matched
     * @param symType  the type to be matched
     * @return a symbol with matching name, scope and type if its found
     */
    operator fun get(id: String, scope: Scope?, symType: SymType): Symbol? {
        val tmp = filter { symType == it.symType }
                .filter { id == it.id }

        if (scope != null) {
            return tmp.filter { it.scope.isPrefix(it.scope) }
                    .maxBy { it.scope.size }
        } else {
            return tmp.firstOrNull()
        }
    }

    /**
     * Gets a symbol with matching name and type.
     *
     * @param name  the name to be matched
     * @param symType  the type to be matched
     * @return a symbol with matching name, scope and type if its found
     */
    operator fun get(name: String, symType: SymType) = this[name, null, symType]
}
