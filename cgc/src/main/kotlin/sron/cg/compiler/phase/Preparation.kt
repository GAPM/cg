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

object Preparation : Phase() {
    private lateinit var state: State

    private var varId = 0

    override fun execute(s: State, init: Init) {
        state = s
        init.prepare()
    }

    private fun Init.prepare() {
        for (fd in funcDef) {
            fd.prepare()
        }
    }

    private fun FuncDef.prepare() {
        val map = mutableMapOf<String, Int>()

        for (arg in args) {
            map[arg.name] = varId++
        }

        for (stmt in stmts) {
            stmt.prepare(map)
        }

        state.varIndex[name] = map
        varId = 0
    }

    private fun Stmt.prepare(map: MutableMap<String, Int>) {
        if (this is VarDec) {
            map[name] = varId++
        }

        when (this) {
            is For -> this.prepare(map)
            is While -> this.prepare(map)
            is If -> this.prepare(map)
        }
    }

    private fun For.prepare(map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(map)
        }
    }

    private fun While.prepare(map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(map)
        }
    }

    private fun If.prepare(map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(map)
        }

        for (elif in elifs) {
            elif.prepare(map)
        }

        elsec?.prepare(map)
    }

    private fun Elif.prepare(map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(map)
        }
    }

    private fun Else.prepare(map: MutableMap<String, Int>) {
        for (stmt in stmts) {
            stmt.prepare(map)
        }
    }
}
