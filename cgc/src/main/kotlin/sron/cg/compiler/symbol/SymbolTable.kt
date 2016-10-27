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
    fun findVariable(id: String, scope: Scope): Variable? {
        return filter { it.symType == SymType.VAR }
                .filter { it.id == id }
                .filter { it.scope.isPrefix(scope) }
                .maxBy { it.scope.size } as? Variable
    }

    fun findVariableInScope(id: String, scope: Scope): Variable? {
        return filter { it.symType == SymType.VAR }
                .filter { it.id == id }
                .filter { it.scope == scope }.firstOrNull() as? Variable
    }

    fun findFunction(id: String, signature: Signature): Function? {
        return filter { it.symType == SymType.FUNC }
                .filter { it.id == id }
                .filter { (it as Function).signature == signature }
                .firstOrNull() as? Function
    }
}
