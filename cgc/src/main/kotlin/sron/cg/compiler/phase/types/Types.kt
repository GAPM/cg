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

package sron.cg.compiler.phase.types

import sron.cg.compiler.Error
import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.nextId
import sron.cg.symbol.Function
import sron.cg.symbol.SymType
import sron.cg.symbol.Variable
import sron.cg.type.CastTable
import sron.cg.type.OpTable
import sron.cg.type.Type

object Types {
    operator fun invoke(state: State, init: Init) = init.types(state)

    private fun Init.types(s: State) {
        for (gvd in glVarDec) {
            gvd.types(s)
        }

        for (fd in funcDef) {
            fd.types(s)
        }
    }

    private fun GlVarDec.types(s: State) {
        if (type == Type.void) {
            s.errors += Error.voidVar(location, name)
            return
        }

        // Initial value handling
        if (exp != null) {
            if (exp.type != type) {
                s.errors += Error.badAssignment(location, exp.type, type)
            }

            if (exp.type == Type.int) {
                try {
                    exp.text.toInt()
                } catch (e: NumberFormatException) {
                    s.errors += Error.outOfRange(exp.location, exp.text)
                }
            } else if (exp.type == Type.float) {
                try {
                    exp.text.toFloat()
                } catch (e: NumberFormatException) {
                    s.errors += Error.outOfRange(exp.location, exp.text)
                }
            }
        }
    }

    private fun FuncDef.types(s: State) {
        scope = "global.$name"
        for (stmt in stmts) {
            stmt.types(s, this, scope)
        }
    }

    private fun Stmt.types(s: State, funcDef: FuncDef, scope: String) {
        when (this) {
            is Expr -> this.types(s, scope)
            is Assignment -> this.types(s, scope)
            is VarDec -> this.types(s, scope)
            is Return -> this.types(s, funcDef, scope)
            is If -> this.types(s, funcDef, scope)
            is For -> this.types(s, funcDef, scope)
            is While -> this.types(s, funcDef, scope)
        }
    }

    private fun Expr.types(s: State, scope: String) {
        when (this) {
            is Literal -> this.types(s)
            is UnaryExpr -> this.types(s, scope)
            is BinaryExpr -> this.types(s, scope)
            is Identifier -> this.types(s, scope)
            is FunctionCall -> this.types(s, scope)
            is Cast -> this.types(s, scope)
            is Graph -> this.types(s, scope)
        }
    }

    private fun Literal.types(s: State) {
        if (type == Type.int) {
            try {
                text.toInt()
            } catch (e: NumberFormatException) {
                s.errors += Error.outOfRange(location, text)
            }
        } else if (type == Type.float) {
            try {
                text.toFloat()
            } catch (e: NumberFormatException) {
                s.errors += Error.outOfRange(location, text)
            }
        }
    }

    private fun UnaryExpr.types(s: State, scope: String) {
        expr.types(s, scope)

        if (expr.type != Type.ERROR) {
            val opType = OpTable.checkUnaryOp(operator, expr.type)

            if (opType != Type.ERROR) {
                this.type = opType
            } else {
                type = Type.ERROR;
                s.errors += Error.badUnaryOp(location, operator, expr.type)
            }
        }
    }

    private fun BinaryExpr.types(s: State, scope: String) {
        lhs.types(s, scope)
        rhs.types(s, scope)

        if (lhs.type != Type.ERROR && rhs.type != Type.ERROR) {
            val opType = OpTable.checkBinaryOp(operator, lhs.type, rhs.type)

            if (opType != Type.ERROR) {
                this.type = opType
            } else {
                type = Type.ERROR
                s.errors += Error.badBinaryOp(location, operator, lhs.type, rhs.type)
            }
        }
    }

    private fun Identifier.types(s: State, scope: String) {
        val qry = s.symbolTable.getSymbol(name, scope, SymType.VAR)

        if (qry != null) {
            val variable = qry as Variable
            type = variable.type
            assignable = true
        } else {
            type = Type.ERROR
            s.errors += Error.notFound(location, name, SymType.VAR)
        }
    }

    private fun FunctionCall.types(s: State, scope: String) {
        expr.map { it.types(s, scope) }
        val qry = s.symbolTable.getSymbol(name, SymType.FUNC)

        if (qry != null) {
            val function = qry as Function

            if (function.args.size > expr.size) {
                type = Type.ERROR
                s.errors += Error.argumentNumber(location, '-', name)
                return
            } else if (function.args.size < expr.size) {
                type = Type.ERROR
                s.errors += Error.argumentNumber(location, '+', name)
                return
            } else {
                for (i in expr.indices) {
                    val eType = expr[i].type
                    val aType = function.args[i].type

                    if (eType != Type.ERROR && (eType != aType)) {
                        type = Type.ERROR
                        s.errors += Error.argument(location, eType, aType, name)
                        return
                    }
                }
            }

            type = function.type
        } else {
            type = Type.ERROR
            s.errors += Error.notFound(location, name, SymType.FUNC)
        }
    }

    private fun Cast.types(s: State, scope: String) {
        expr.types(s, scope)

        if (!CastTable.check(type, expr.type)) {
            s.errors += Error.cast(location, type, expr.type)
            type = Type.ERROR
            return
        }
    }

    private fun Graph.types(s: State, scope: String) {
        type = when (gtype) {
            GraphType.GRAPH -> Type.graph
            GraphType.DIGRAPH -> Type.digraph
        }

        num.types(s, scope)
        if (num.type != Type.int) {
            //TODO: throw error
        }

        for (edge in edges) {
            edge.source.types(s, scope)
            edge.target.types(s, scope)

            if (edge.source.type != Type.int) {
                //TODO: throw error
            }

            if (edge.target.type != Type.int) {
                //TODO: throw error
            }
        }
    }

    private fun Assignment.types(s: State, scope: String) {
        lhs.types(s, scope)
        rhs.types(s, scope)

        if (lhs.type != Type.ERROR && rhs.type != Type.ERROR) {
            if (!lhs.assignable) {
                s.errors += Error.nonAssignable(location)
                return
            }

            if (lhs.type != rhs.type) {
                s.errors += Error.badAssignment(location, lhs.type, rhs.type)
                return
            }
        }
    }

    private fun VarDec.types(s: State, scope: String) {
        val qry = s.symbolTable.getSymbol(name, scope, SymType.VAR)

        if (qry == null) {
            var variable = Variable(name, type, scope, location)
            s.symbolTable.addSymbol(variable)

            expr?.let {
                expr.types(s, scope)
                if (expr.type != Type.ERROR && type != expr.type) {
                    s.errors += Error.badAssignment(location, type, expr.type)
                }
            }
        } else {
            s.errors += Error.redeclaration(location, qry.location, name, SymType.VAR)
        }
    }

    private fun Return.types(s: State, funcDef: FuncDef, scope: String) {
        expr?.let {
            it.types(s, scope)

            if (it.type != Type.ERROR && it.type != funcDef.type) {
                s.errors += Error.badReturn(location, it.type, funcDef.type, funcDef.name)
            }
        }
    }

    private fun If.types(s: State, funcDef: FuncDef, scope: String) {
        cond.types(s, scope)

        if (cond.type != Type.ERROR && cond.type != Type.bool) {
            s.errors += Error.nonBoolCondition(cond.location, cond.type)
        }

        this.scope = "$scope.if${nextId()}"

        for (stmt in stmts) {
            stmt.types(s, funcDef, this.scope)
        }

        for (elif in elifs) {
            elif.types(s, funcDef, scope)
        }

        elsec?.types(s, funcDef, scope)
    }

    private fun Elif.types(s: State, funcDef: FuncDef, scope: String) {
        cond.types(s, scope)

        if (cond.type != Type.ERROR && cond.type != Type.bool) {
            s.errors += Error.nonBoolCondition(cond.location, cond.type)
        }

        this.scope = "$scope.elif${nextId()}"

        for (stmt in stmts) {
            stmt.types(s, funcDef, this.scope)
        }
    }

    private fun Else.types(s: State, funcDef: FuncDef, scope: String) {
        this.scope = "$scope.else${nextId()}"

        for (stmt in stmts) {
            stmt.types(s, funcDef, this.scope)
        }
    }

    private fun For.types(s: State, funcDef: FuncDef, scope: String) {
        initial.types(s, scope)
        cond.types(s, scope)
        mod.types(s, scope)

        if (cond.type != Type.ERROR && cond.type != Type.bool) {
            s.errors += Error.nonBoolCondition(cond.location, cond.type)
        }

        this.scope = "$scope.for${nextId()}"

        for (stmt in stmts) {
            stmt.types(s, funcDef, this.scope)
        }
    }

    private fun While.types(s: State, funcDef: FuncDef, scope: String) {
        cond.types(s, scope)

        if (cond.type != Type.ERROR && cond.type != Type.bool) {
            s.errors += Error.nonBoolCondition(cond.location, cond.type)
        }

        this.scope = "$scope.while${nextId()}"

        for (stmt in stmts) {
            stmt.types(s, funcDef, this.scope)
        }
    }
}
