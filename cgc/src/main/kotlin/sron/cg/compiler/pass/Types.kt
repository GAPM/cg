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
import sron.cg.compiler.lang.*
import sron.cg.compiler.symbol.Signature.Companion.signature
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

        val function = state.symbolTable.findFunction(id, args.signature())

        if (error) {
            type = AtomType.ERROR
        } else {
            if (function != null) {
                type = function.type
            } else {
                type = AtomType.ERROR
                state.errors += FunctionNotFound(this)
            }
        }
    }

    private fun Cast.types() {
        expr.types()

        if (expr.type != AtomType.ERROR && !Casts.isValid(expr.type, type)) {
            type = AtomType.ERROR
            state.errors += InvalidCast(this)
        }
    }

    private fun ArrayLit.types() {
        val first = elems[0].type
        for (i in 1..elems.size - 1) {
            if (elems[i].type != first) {
                type = AtomType.ERROR
                state.errors += ArrayLiteralTypeMismatch(this)
                break
            }
        }
        type = first
    }

    private fun ArrayAccess.types() {
        array.types()
        subscript.types()

        if (subscript.type != AtomType.int) {
            type = AtomType.ERROR
            state.errors += SubscriptTypeNotInt(this)
        } else if (array is VarName) { // Array is a variable
            val variable = state.symbolTable.findVariable(array.id, scope)
            if (variable != null) {
                if (variable.type is ArrayType) {
                    type = variable.type.innerType
                } else {
                    type = AtomType.ERROR
                    state.errors += TypeNotSubscriptable(this)
                }
            } else {
                type = AtomType.ERROR
                state.errors += VariableNotFoundInScope(array)
            }
        } else if (array is ArrayLit) { // Array is a literal
            if (array.type == AtomType.ERROR) {
                type = AtomType.ERROR
            } else {
                type = (array.type as ArrayType).innerType
            }
        } else {
            type = AtomType.ERROR
            state.errors += TypeNotSubscriptable(this)
        }
    }

    private fun UnaryExpr.types() {
        expr.types()

        if (expr.type != AtomType.ERROR) {
            val result = Operations.findUnary(op, expr.type)

            if (result != AtomType.ERROR) {
                type = result
            } else {
                type = AtomType.ERROR
                state.errors += InvalidUnaryExpr(this)
            }
        } else {
            type = AtomType.ERROR
        }
    }

    private fun BinaryExpr.types() {
        lhs.types()
        rhs.types()

        if (lhs.type != AtomType.ERROR && rhs.type != AtomType.ERROR) {
            val result = Operations.findBinary(op, lhs.type to rhs.type)

            if (result != AtomType.ERROR) {
                type = result
            } else {
                type = AtomType.ERROR
                state.errors += InvalidBinaryExpr(this)
            }
        } else {
            type = AtomType.ERROR
        }
    }

    private fun Expr.types() = when (this) {
        is Literal -> {
        }//Type already known
        is VarName -> this.types()
        is GraphLit -> this.types()
        is FunctionCall -> this.types()
        is Cast -> this.types()
        is ArrayLit -> this.types()
        is ArrayAccess -> this.types()
        is UnaryExpr -> this.types()
        is BinaryExpr -> this.types()

        else -> throw IllegalStateException()
    }

    private fun VarDec.types() {
        expr?.let { expr.types() }
        /* Type inference happens here */

        // Declaration does not have type and expression is not present
        if ((type == AtomType.ERROR && expr == null) || // or
                // expression is present but it's type can not be infered
                (expr != null && expr.type == AtomType.ERROR)) {
            state.errors += CanNotInferType(this)
        } else if (type == AtomType.ERROR && expr != null
                && expr.type != AtomType.ERROR) {
            type = expr.type
            state.symbolTable += Variable(id, type, scope, location)
        } else if (type != AtomType.ERROR && expr != null &&
                expr.type != type) {
            state.errors += AssignmentTypeMismatch(this, type, expr.type)
        } else {
            state.symbolTable += Variable(id, type, scope, location)
        }
    }

    private fun Assignment.types() {
        lhs.types()
        rhs.types()

        if (lhs is VarName || lhs is ArrayAccess) {
            if (lhs.type != AtomType.ERROR && rhs.type != AtomType.ERROR &&
                    lhs.type != rhs.type) {
                state.errors += AssignmentTypeMismatch(this, lhs.type, rhs.type)
            }
        } else {
            state.errors += NonAssignableExpression(this)
        }
    }

    private fun Return.types() {
        expr?.let {
            it.types()

            val fd = it.funcDef
            if (it.type != AtomType.ERROR && it.type != fd.type) {
                state.errors += InvalidReturn(this)
            }
        }
    }

    private fun Print.types() {
        expr.types()
    }

    private fun Assert.types() {
        expr.types()

        if (expr.type != AtomType.ERROR && expr.type != AtomType.bool) {
            state.errors += NonBoolCondition(expr, "assert")
        }
    }

    private fun If.types() {
        expr.types()

        if (expr.type != AtomType.ERROR && expr.type != AtomType.bool) {
            state.errors += NonBoolCondition(expr, "if")
        }

        for (stmt in body) {
            stmt.types()
        }
    }

    private fun Elif.types() {
        expr.types()

        if (expr.type != AtomType.ERROR && expr.type != AtomType.bool) {
            state.errors += NonBoolCondition(expr, "elif")
        }

        for (stmt in body) {
            stmt.types()
        }
    }

    private fun Else.types() {
        for (stmt in body) {
            stmt.types()
        }
    }

    private fun IfBlock.types() {
        ifc.types()
        elif.forEach { it.types() }
        elsec?.types()
    }

    private fun For.types() {
        initial.types()
        condition.types()
        modifier.types()

        if (condition.type != AtomType.ERROR && condition.type != AtomType.bool) {
            state.errors += NonBoolCondition(condition, "for")
        }

        for (stmt in body) {
            stmt.types()
        }
    }

    private fun While.types() {
        condition.types()

        if (condition.type != AtomType.ERROR && condition.type != AtomType.bool) {
            state.errors += NonBoolCondition(condition, "while")
        }

        for (stmt in body) {
            stmt.types()
        }
    }

    private fun Stmt.types() = when (this) {
        is VarDec -> this.types()
        is Assignment -> this.types()
        is Return -> this.types()
        is Print -> this.types()
        is Assert -> this.types()
        is IfBlock -> this.types()
        is For -> this.types()
        is While -> this.types()
        is Control -> {
        }

        else -> throw IllegalStateException()
    }

    private fun FuncDef.types() {
        for (stmt in body) {
            stmt.types()
        }
    }

    override fun exec(ast: Init) {
        for (vd in ast.varDec) {
            vd.types()
        }

        for (fd in ast.funcDef) {
            fd.types()
        }
    }
}
