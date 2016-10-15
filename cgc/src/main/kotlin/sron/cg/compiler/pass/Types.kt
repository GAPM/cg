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
import sron.cg.compiler.symbol.Signature
import sron.cg.compiler.symbol.Variable

class Types(state: State) : Pass(state) {
    private fun VarName.types() {
        val variable = state.symbolTable.findVariable(id, scope)
        if (variable == null) {
            type = AtomType.ERROR
            state.errors += VariableNotFoundInScope(this)
        } else {
            type = variable.type
        }
    }

    private fun GraphLit.types() {
        type = when (graphType) {
            GraphType.GRAPH -> AtomType.graph
            GraphType.DIGRAPH -> AtomType.digraph
        }

        size.types()
        if (size.type != AtomType.int) {
            type = AtomType.ERROR
            state.errors += NonIntegerSize(this)
        }

        for (edge in edges) {
            edge.source.types()
            edge.target.types()

            if (edge.source.type != AtomType.int) {
                type = AtomType.ERROR
                state.errors += NonIntegerNode(edge.source)
            }

            if (edge.target.type != AtomType.int) {
                type = AtomType.ERROR
                state.errors += NonIntegerNode(edge.target)
            }
        }
    }

    private fun FunctionCall.types() {
        var error = false

        args.forEach {
            it.types()
            if (it.type == AtomType.ERROR) {
                error = true
            }
        }
        val signature = Signature(args.map { it.type })
        val function = state.symbolTable.findFunction(id, signature)

        if (function != null && !error) {
            //todo
        } else {
            type = AtomType.ERROR
            //todo ERROR: function not found
        }
    }

    private fun Expr.types() {
        when (this) {
            is Literal -> {
            }//Type already known
            is VarName -> this.types()
            is GraphLit -> this.types()
            is FunctionCall -> this.types()

            else -> throw IllegalStateException()
        }
    }

    private fun VarDec.types() {
        expr?.let { expr.types() }

        if (type == AtomType.UNKNOWN && expr == null) {
            //todo ERROR: can not infer
        } else if (type == AtomType.UNKNOWN && expr != null
                && expr.type != AtomType.ERROR) {
            type = expr.type
            state.symbolTable += Variable(id, type, scope, location)
        } else if (type != AtomType.UNKNOWN && expr != null &&
                expr.type != type) {
            //todo ERROR: assignment type mismatch
        } else {
            state.symbolTable += Variable(id, type, scope, location)
        }
    }

    override fun exec(ast: Init) {
        for (vd in ast.varDec) {
            vd.types()
        }
    }
}
