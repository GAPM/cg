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

import sron.cg.compiler.Error
import sron.cg.compiler.State
import sron.cg.compiler.ast.FuncDef
import sron.cg.compiler.ast.GlVarDec
import sron.cg.compiler.ast.Init
import sron.cg.compiler.phase.globals.runtimeFunctions
import sron.cg.compiler.symbol.Function
import sron.cg.compiler.symbol.SymType
import sron.cg.compiler.symbol.Variable
import sron.cg.compiler.type.Type

object Globals : Phase() {
    private lateinit var state: State

    override fun execute(s: State, init: Init) {
        state = s
        init.globals()
    }

    private fun Init.globals() {
        for (f in runtimeFunctions) {
            state.symbolTable += f
        }

        for (gvd in glVarDec) {
            gvd.globals()
        }

        for (fd in funcDef) {
            fd.globals()
        }

        val qry = state.symbolTable["main", SymType.FUNC]
        val noEntry = when (qry) {
            is Function -> (qry.type != Type.void && qry.args.size != 0)
            else -> false
        }

        if (noEntry) {
            state.errors += Error.noEntryPoint(location)
        }
    }

    private fun FuncDef.globals() {
        val qry = state.symbolTable[name, SymType.FUNC]

        if (qry == null) {
            val args = Array(args.size) { i ->
                Variable(args[i].name, args[i].type, "global.$name", args[i].location)
            }

            val func = Function(name, "global", type, location, *args)
            state.symbolTable += func
            args.map {
                state.symbolTable += it
            }
        } else {
            state.errors += Error.redeclaration(location, qry.location, name, SymType.FUNC)
        }
    }

    private fun GlVarDec.globals() {
        val qry = state.symbolTable[this.name, SymType.FUNC]

        if (qry == null) {
            val glVar = Variable(this.name, this.type, "global", this.location)
            state.symbolTable += glVar
        } else {
            state.errors += Error.redeclaration(this.location, qry.location, this.name, SymType.VAR)
        }
    }
}
