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

package sron.cg.compiler

import sron.cg.symbol.SymbolTable
import sron.cg.symbol.Runtime
import java.util.*

class State(val parameters: Parameters) {
    val errors = ArrayList<String>()
    val symbolTable = SymbolTable()
    val varIndex = mutableMapOf<String, MutableMap<String, Int>>()

    init {
        for (f in Runtime.functions) {
            symbolTable.addSymbol(f)
        }
    }
}
