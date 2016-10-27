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

import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.error.*
import sron.cg.compiler.lang.AtomType
import sron.cg.compiler.symbol.Function
import sron.cg.compiler.symbol.Variable

class Globals(state: State) : Pass(state) {
    private fun VarDec.globals() {
        if (expr != null) {
            state.errors += ExpressionForbidden(this)
        }

        if (type == AtomType.ERROR) {
            state.errors += GlobalVarMissingType(this)
        } else {
            val gv = state.symbolTable.findVariable(id, scope)
            if (gv != null) {
                state.errors += VariableRedeclaration(this, gv)
            } else {
                state.symbolTable += Variable(id, type, scope, location)
            }
        }
    }

    private fun FuncDef.globals() {
        val qry = state.symbolTable.findFunction(id, signature)

        if (qry != null) {
            state.errors += FunctionRedefinition(this, qry)
            return
        }
        val paramList = arrayListOf<Variable>()

        for (param in params) {
            val prm = state.symbolTable.findVariableInScope(param.id, param.scope)
            if (prm != null) {
                state.errors += ParameterRedefinition(param, prm)
            } else {
                val new = Variable(param.id, param.type, param.scope, param.location)
                state.symbolTable += new
                paramList += new
            }
        }

        state.symbolTable += Function(id, type, paramList, scope, location)
    }

    override fun exec(ast: Init) {
        for (vd in ast.varDec) {
            vd.globals()
        }

        for (fd in ast.funcDef) {
            fd.globals()
        }
    }
}
