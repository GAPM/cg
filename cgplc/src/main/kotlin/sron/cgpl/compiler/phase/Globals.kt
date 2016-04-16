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

package sron.cgpl.compiler.phase

import sron.cgpl.compiler.Error
import sron.cgpl.compiler.State
import sron.cgpl.compiler.ast.FuncDef
import sron.cgpl.compiler.ast.GlVarDec
import sron.cgpl.compiler.ast.Init
import sron.cgpl.symbol.Function
import sron.cgpl.symbol.SymType
import sron.cgpl.symbol.Variable

object Globals {

    operator fun invoke(state: State, init: Init) = init.globals(state)

    private fun Init.globals(state: State) {
        for (gvd in glVarDec) {
            gvd.globals(state)
        }

        for (fd in funcDef) {
            fd.globals(state)
        }
    }

    private fun FuncDef.globals(s: State) {
        val qry = s.symbolTable.getSymbol(name, SymType.FUNC)

        if (qry == null) {
            for (arg in args) {
                val v = Variable(arg.name, arg.type, "global.${this.name}", arg.location)
                s.symbolTable.addSymbol(v)
            }

            val func = Function(name, "global", type, location)
            s.symbolTable.addSymbol(func)
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
