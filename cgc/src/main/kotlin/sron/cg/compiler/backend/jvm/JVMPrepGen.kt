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

package sron.cg.compiler.backend.jvm

import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.pass.Pass

class JVMPrepGen(state: State) : Pass(state) {
    private lateinit var map: MutableMap<String, Int>
    private var idx = 0

    private fun If.prep() {
        for (stmt in body) {
            stmt.prep()
        }
    }

    private fun Elif.prep() {
        for (stmt in body) {
            stmt.prep()
        }
    }

    private fun Else.prep() {
        for (stmt in body) {
            stmt.prep()
        }
    }

    private fun IfBlock.prep() {
        ifc.prep()
        elif.forEach { it.prep() }
        elsec?.prep()
    }

    private fun For.prep() {
        for (stmt in body) {
            stmt.prep()
        }
    }

    private fun While.prep() {
        for (stmt in body) {
            stmt.prep()
        }
    }

    private fun Stmt.prep() = when (this) {
        is VarDec -> map[this.id] = idx++
        is IfBlock -> prep()
        is For -> prep()
        is While -> prep()

        else -> { // Do nothing
        }
    }

    private fun FuncDef.prep() {
        val function = state.symbolTable.findFunction(id, signature) ?:
                throw IllegalStateException()
        map = mutableMapOf<String, Int>()

        for (param in params) {
            map[param.id] = idx++
        }

        for (stmt in body) {
            stmt.prep()
        }

        JVM.varIndex[function] = map
    }

    override fun exec(ast: Init) {
        for (fd in ast.funcDef) {
            fd.prep()
        }
    }
}
