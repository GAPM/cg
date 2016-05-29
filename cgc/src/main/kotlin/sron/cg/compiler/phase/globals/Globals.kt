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

package sron.cg.compiler.phase.globals

import sron.cg.compiler.Error
import sron.cg.compiler.State
import sron.cg.compiler.ast.FuncDef
import sron.cg.compiler.ast.GlVarDec
import sron.cg.compiler.ast.Init
import sron.cg.compiler.phase.globals.helper.rtFunctions
import sron.cg.symbol.Function
import sron.cg.symbol.SymType
import sron.cg.symbol.Variable
import sron.cg.type.Type

object Globals {
    operator fun invoke(state: State, init: Init) = init.globals(state)

    private fun Init.globals(s: State) {

        for (f in rtFunctions) {
            s.symbolTable.addSymbol(f)
        }

        for (gvd in glVarDec) {
            gvd.globals(s)
        }

        for (fd in funcDef) {
            fd.globals(s)
        }

        var noEntry = false
        val qry = s.symbolTable.getSymbol("main", SymType.FUNC)
        when (qry) {
            null -> noEntry = true
            is Function -> {
                if (qry.type != Type.void || qry.args.size != 0) {
                    noEntry = true
                }
            }
        }

        if (noEntry) {
            s.errors += Error.noEntryPoint(location)
        }
    }

    private fun FuncDef.globals(s: State) {
        val qry = s.symbolTable.getSymbol(name, SymType.FUNC)

        if (qry == null) {
            val args = Array(args.size) { i ->
                Variable(args[i].name, args[i].type, "global.$name", args[i].location)
            }

            val func = Function(name, "global", type, location, *args)
            s.symbolTable.addSymbol(func)
            args.map { s.symbolTable.addSymbol(it) }
        } else {
            s.errors += Error.redeclaration(location, qry.location, name, SymType.FUNC)
        }
    }

    private fun GlVarDec.globals(s: State) {
        val qry = s.symbolTable.getSymbol(this.name, SymType.FUNC)

        if (qry == null) {
            val glVar = Variable(this.name, this.type, "global", this.location)
            s.symbolTable.addSymbol(glVar)
        } else {
            s.errors += Error.redeclaration(this.location, qry.location, this.name, SymType.VAR)
        }
    }
}
