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

package sron.cgpl.compiler.ast

import sron.cgpl.compiler.State
import sron.cgpl.symbol.Location
import java.util.*

class Init : ASTNode(Location(0)) {
    val glVarDec = ArrayList<GlVarDec>()
    val funcDef = ArrayList<FuncDef>()

    fun globals(s: State) {
        for (gvd in glVarDec) {
            gvd.globals(s)
        }

        for (fd in funcDef) {
            fd.globals(s)
        }
    }

    fun structure(s: State) {
        for (fd in funcDef) {
            fd.structure(s)
        }
    }
}
