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

package sron.cg.compiler.pass

import sron.cg.compiler.*
import sron.cg.compiler.ast.*
import sron.cg.compiler.ast.Init
import sron.cg.compiler.lang.AtomType
import sron.cg.compiler.symbol.Function
import sron.cg.compiler.symbol.Variable

class Globals(state: State) : Pass(state) {

    private fun FuncDef.globals() {
        val qry = state.symbolTable.findFunction(id, signature)

        if (qry == null) {
            val paramsVars = params.map {
                Variable(it.id, it.type, scope, it.location)
            }

            val fd = Function(id, type, paramsVars, scope, location)

            fd.params.forEach { state.symbolTable += it }
            state.symbolTable += fd
        } else {
            state.errors += FunctionRedefinition(this, qry)
        }
    }

    private fun VarDec.globals() {
        if (type != AtomType.UNKNOWN) {
            val qry = state.symbolTable.findVariable(id, scope)

            if (qry != null && scope == qry.scope) {
                state.errors += VariableRedeclaration(this, qry)
            } else {
                state.symbolTable += Variable(id, type, scope, location)
            }
        } else {
            state.errors += GlobalVarMissingType(this)
        }
    }

    override fun exec(ast: Init) {
        for (fd in ast.funcDef) {
            fd.globals()
        }

        for (vd in ast.varDec) {
            vd.globals()
        }
    }
}
