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

package sron.cg.compiler.phase

import sron.cg.compiler.State
import sron.cg.compiler.ast.*

class Preparation(private val s: State, private val init: Init) : Phase {
    private var varId = 0

    override fun execute() = init.prepare(s)

    private fun Init.prepare(s: State) {
        for (fd in funcDef) {
            fd.prepare(s)
        }
    }

    private fun FuncDef.prepare(s: State) {
        val map = mutableMapOf<String, Int>()

        for (arg in args) {
            map[arg.name] = varId++
        }

        for (stmt in stmts) {
            stmt.prepare(s, map)
        }

        s.varIndex[name] = map
        varId = 0
    }

    private fun Stmt.prepare(s: State, map: MutableMap<String, Int>) {
        if (this is VarDec) {
            map[name] = varId++
        }

        when (this) {
            is For -> this.prepare(s, map)
            is While -> this.prepare(s, map)
            is If -> this.prepare(s, map)
        }
    }

    private fun For.prepare(s: State, map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(s, map)
        }
    }

    private fun While.prepare(s: State, map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(s, map)
        }
    }

    private fun If.prepare(s: State, map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(s, map)
        }

        for (elif in elifs) {
            elif.prepare(s, map)
        }

        elsec?.prepare(s, map)
    }

    private fun Elif.prepare(s: State, map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(s, map)
        }
    }

    private fun Else.prepare(s: State, map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(s, map)
        }
    }
}
