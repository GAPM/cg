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

import sron.cg.compiler.Compiler.Companion.nextID
import sron.cg.compiler.Error
import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.symbol.Function
import sron.cg.compiler.symbol.SymType
import sron.cg.compiler.symbol.Variable
import sron.cg.compiler.type.CastTable
import sron.cg.compiler.type.OpTable
import sron.cg.compiler.type.Type

object Types : Phase() {
    private lateinit var state: State

    override fun execute(s: State, init: Init) {
        state = s
        init.types()
    }

    private fun Init.types() {
        for (fd in funcDef) {
            fd.types()
        }

        for (gvd in glVarDec) {
            gvd.types()
        }
    }

    private fun FuncDef.types() {
        scope = "global.$name"
        for (stmt in stmts) {
            stmt.types(this, scope)
        }
    }

    private fun GlVarDec.types() {
        if (type == Type.void) {
            state.errors += Error.voidVar(location, name)
            return
        }

        // Initial value handling
        if (exp != null) {
            if (exp.type != type) {
                state.errors += Error.badAssignment(location, exp.type, type)
            }

            if (exp.type == Type.int) {
                try {
                    exp.text.toInt()
                } catch (e: NumberFormatException) {
                    state.errors += Error.outOfRange(exp.location, exp.text)
                }
            } else if (exp.type == Type.float) {
                try {
                    exp.text.toFloat()
                } catch (e: NumberFormatException) {
                    state.errors += Error.outOfRange(exp.location, exp.text)
                }
            }
        }
    }

    private fun Stmt.types(funcDef: FuncDef, scope: String) {
        when (this) {
            is Expr -> this.types(scope)
            is Assignment -> this.types(scope)
            is VarDec -> this.types(scope)
            is Return -> this.types(funcDef, scope)
            is If -> this.types(funcDef, scope)
            is For -> this.types(funcDef, scope)
            is While -> this.types(funcDef, scope)
            is Print -> this.types(scope)
            is Assertion -> this.types(scope)
        }
    }

    private fun Expr.types(scope: String) {
        when (this) {
            is Literal -> this.types()
            is UnaryExpr -> this.types(scope)
            is BinaryExpr -> this.types(scope)
            is Identifier -> this.types(scope)
            is FunctionCall -> this.types(scope)
            is Cast -> this.types(scope)
            is Graph -> this.types(scope)
        }
    }

    private fun Literal.types() {
        if (type == Type.int) {
            try {
                text.toInt()
            } catch (e: NumberFormatException) {
                state.errors += Error.outOfRange(location, text)
            }
        } else if (type == Type.float) {
            try {
                text.toFloat()
            } catch (e: NumberFormatException) {
                state.errors += Error.outOfRange(location, text)
            }
        }
    }

    private fun UnaryExpr.types(scope: String) {
        expr.types(scope)

        if (expr.type != Type.ERROR) {
            val opType = OpTable.checkUnaryOp(operator, expr.type)

            if (opType != Type.ERROR) {
                this.type = opType
            } else {
                type = Type.ERROR
                state.errors += Error.badUnaryOp(location, operator, expr.type)
            }
        }
    }

    private fun BinaryExpr.types(scope: String) {
        lhs.types(scope)
        rhs.types(scope)

        if (lhs.type != Type.ERROR && rhs.type != Type.ERROR) {
            val opType = OpTable.checkBinaryOp(operator, lhs.type, rhs.type)

            if (opType != Type.ERROR) {
                this.type = opType
            } else {
                type = Type.ERROR
                state.errors += Error.badBinaryOp(location, operator, lhs.type, rhs.type)
            }
        }
    }

    private fun Identifier.types(scope: String) {
        val qry = state.symbolTable[name, scope, SymType.VAR]

        if (qry != null) {
            val variable = qry as Variable
            type = variable.type
            assignable = true
            referencedVar = variable
        } else {
            type = Type.ERROR
            state.errors += Error.notFound(location, name, SymType.VAR)
        }
    }

    private fun FunctionCall.types(scope: String) {
        expr.map { it.types(scope) }
        val qry = state.symbolTable[name, SymType.FUNC]

        if (qry != null) {
            val function = qry as Function

            if (function.name == "main" && function.args.size == 0) {
                state.errors += Error.callingEntryPoint(location)
            }

            if (function.args.size > expr.size) {
                type = Type.ERROR
                state.errors += Error.argumentNumber(location, '-', name)
                return
            } else if (function.args.size < expr.size) {
                type = Type.ERROR
                state.errors += Error.argumentNumber(location, '+', name)
                return
            } else {
                for (i in expr.indices) {
                    val eType = expr[i].type
                    val aType = function.args[i].type

                    if (eType != Type.ERROR && (eType != aType)) {
                        type = Type.ERROR
                        state.errors += Error.argument(location, eType, aType, name)
                        return
                    }
                }
            }

            type = function.type
        } else {
            type = Type.ERROR
            state.errors += Error.notFound(location, name, SymType.FUNC)
        }
    }

    private fun Cast.types(scope: String) {
        expr.types(scope)

        if (!CastTable.check(expr.type, type)) {
            state.errors += Error.cast(location, type, expr.type)
            type = Type.ERROR
            return
        }
    }

    private fun Graph.types(scope: String) {
        type = when (gtype) {
            GraphType.GRAPH -> Type.graph
            GraphType.DIGRAPH -> Type.digraph
        }

        num.types(scope)
        if (num.type != Type.int) {
            state.errors += Error.nonIntegerSize(location)
        }

        for (edge in edges) {
            edge.source.types(scope)
            edge.target.types(scope)

            if (edge.source.type != Type.int) {
                state.errors += Error.nonIntegerNode(edge.location)
            }

            if (edge.target.type != Type.int) {
                state.errors += Error.nonIntegerNode(edge.location)
            }
        }
    }

    private fun Assignment.types(scope: String) {
        lhs.types(scope)
        rhs.types(scope)

        if (lhs.type != Type.ERROR && rhs.type != Type.ERROR) {
            if (!lhs.assignable) {
                state.errors += Error.nonAssignable(location)
                return
            }

            if (lhs.type != rhs.type) {
                state.errors += Error.badAssignment(location, lhs.type, rhs.type)
                return
            }
        }
    }

    private fun VarDec.types(scope: String) {
        val qry = state.symbolTable[name, scope, SymType.VAR]

        if (qry == null) {
            exp?.types(scope)

            val finalType = if (type == Type.ERROR && exp == null) {
                state.errors += Error.badInference(location, name)
                Type.ERROR
            } else if (type == Type.ERROR && exp != null) {
                exp.type
            } else if (type != Type.ERROR && exp == null) {
                type
            } else if (type != Type.ERROR && exp != null && type != exp.type) {
                state.errors += Error.badAssignment(location, type, exp.type)
                Type.ERROR
            } else {
                type
            }

            if (finalType != Type.ERROR) {
                type = finalType
                val variable = Variable(name, finalType, scope, location)
                state.symbolTable += variable
            }

        } else {
            state.errors += Error.redeclaration(location, qry.location, name, SymType.VAR)
        }
    }

    private fun Return.types(funcDef: FuncDef, scope: String) {
        expr?.let {
            it.types(scope)

            if (it.type != Type.ERROR && it.type != funcDef.type) {
                state.errors += Error.badReturn(location, it.type, funcDef.type, funcDef.name)
            }
        }
    }

    private fun If.types(funcDef: FuncDef, scope: String) {
        cond.types(scope)

        if (cond.type != Type.ERROR && cond.type != Type.bool) {
            state.errors += Error.nonBoolCondition(cond.location, cond.type)
        }

        this.scope = "$scope.if${nextID()}"

        for (stmt in stmts) {
            stmt.types(funcDef, this.scope)
        }

        for (elif in elifs) {
            elif.types(funcDef, scope)
        }

        elsec?.types(funcDef, scope)
    }

    private fun Elif.types(funcDef: FuncDef, scope: String) {
        cond.types(scope)

        if (cond.type != Type.ERROR && cond.type != Type.bool) {
            state.errors += Error.nonBoolCondition(cond.location, cond.type)
        }

        this.scope = "$scope.elif${nextID()}"

        for (stmt in stmts) {
            stmt.types(funcDef, this.scope)
        }
    }

    private fun Else.types(funcDef: FuncDef, scope: String) {
        this.scope = "$scope.else${nextID()}"

        for (stmt in stmts) {
            stmt.types(funcDef, this.scope)
        }
    }

    private fun For.types(funcDef: FuncDef, scope: String) {
        initial.types(scope)
        cond.types(scope)
        mod.types(scope)

        if (cond.type != Type.ERROR && cond.type != Type.bool) {
            state.errors += Error.nonBoolCondition(cond.location, cond.type)
        }

        this.scope = "$scope.for${nextID()}"

        for (stmt in stmts) {
            stmt.types(funcDef, this.scope)
        }
    }

    private fun While.types(funcDef: FuncDef, scope: String) {
        cond.types(scope)

        if (cond.type != Type.ERROR && cond.type != Type.bool) {
            state.errors += Error.nonBoolCondition(cond.location, cond.type)
        }

        this.scope = "$scope.while${nextID()}"

        for (stmt in stmts) {
            stmt.types(funcDef, this.scope)
        }
    }

    private fun Print.types(scope: String) {
        expr.types(scope)
    }

    private fun Assertion.types(scope: String) {
        expr.types(scope)

        if (expr.type != Type.ERROR) {
            if (expr.type != Type.bool) {
                state.errors += Error.badAssertionType(expr.location, expr.type)
            }
        }
    }
}
