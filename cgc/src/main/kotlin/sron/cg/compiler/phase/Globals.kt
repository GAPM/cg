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

    override fun execute(s: State, init: Init) = init.globals(s)

    private fun Init.globals(s: sron.cg.compiler.State) {
        for (f in runtimeFunctions) {
            s.symbolTable += f
        }

        for (gvd in glVarDec) {
            gvd.globals(s)
        }

        for (fd in funcDef) {
            fd.globals(s)
        }

        val qry = s.symbolTable["main", SymType.FUNC]
        val noEntry = when (qry) {
            is Function -> (qry.type != Type.void && qry.args.size != 0)
            else -> false
        }

        if (noEntry) {
            s.errors += Error.noEntryPoint(location)
        }
    }

    private fun FuncDef.globals(s: State) {
        val qry = s.symbolTable[name, SymType.FUNC]

        if (qry == null) {
            val args = Array(args.size) { i ->
                Variable(args[i].name, args[i].type, "global.$name", args[i].location)
            }

            val func = Function(name, "global", type, location, *args)
            s.symbolTable += func
            args.map {
                s.symbolTable += it
            }
        } else {
            s.errors += Error.redeclaration(location, qry.location, name, SymType.FUNC)
        }
    }

    private fun GlVarDec.globals(s: State) {
        val qry = s.symbolTable[this.name, SymType.FUNC]

        if (qry == null) {
            val glVar = Variable(this.name, this.type, "global", this.location)
            s.symbolTable += glVar
        } else {
            s.errors += Error.redeclaration(this.location, qry.location, this.name, SymType.VAR)
        }
    }
}
