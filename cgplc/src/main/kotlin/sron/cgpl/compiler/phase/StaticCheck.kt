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

import sron.cgpl.compiler.*
import sron.cgpl.compiler.internal.GrpParser.*
import sron.cgpl.symbol.Function
import sron.cgpl.symbol.Location
import sron.cgpl.symbol.SymType
import sron.cgpl.symbol.Variable
import sron.cgpl.type.CastTable
import sron.cgpl.type.OpTable
import sron.cgpl.type.Type
import sron.cgpl.type.toGrpType

class StaticCheck : Phase() {

    override fun exitInteger(ctx: IntegerContext) {
        super.exitInteger(ctx)

        val text = ctx.IntLit().text
        val location = Location(ctx.IntLit())

        try {
            ctx.IntLit().text.toLong()
            setType(ctx, Type.int)
        } catch (e: NumberFormatException) {
            error(OutOfRange(location, text))
            setType(ctx, Type.ERROR)
        }
    }

    override fun exitFloat(ctx: FloatContext) {
        super.exitFloat(ctx)

        val text = ctx.FloatLit().text
        val location = Location(ctx.FloatLit())

        try {
            ctx.FloatLit().text.toDouble()
            setType(ctx, Type.float)
        } catch (e: NumberFormatException) {
            error(OutOfRange(location, text))
            setType(ctx, Type.ERROR)
        }
    }

    override fun exitBoolean(ctx: BooleanContext) {
        super.exitBoolean(ctx)
        setType(ctx, Type.bool)
    }

    override fun exitCharacter(ctx: CharacterContext) {
        super.exitCharacter(ctx)
        setType(ctx, Type.char)
    }

    override fun exitString(ctx: StringContext) {
        super.exitString(ctx)
        setType(ctx, Type.string)
    }

    override fun exitVarName(ctx: VarNameContext) {
        super.exitVarName(ctx)

        val name = ctx.Identifier().text
        val location = Location(ctx.Identifier())
        val scope = scopeUID()

        val qry = symTab.getSymbol(name, scope, SymType.VAR)
        when (qry) {
            null -> {
                error(NotFound(location, name, SymType.VAR))
                setType(ctx, Type.ERROR)
            }
            else -> {
                val v = qry as Variable
                setType(ctx, v.type)
                setAssignable(ctx, true)
            }
        }
    }

    override fun exitFuncCall(ctx: FuncCallContext) {
        super.exitFuncCall(ctx)

        val name = ctx.Identifier().text
        val location = Location(ctx.Identifier())
        var errorFound = false

        val qry = symTab.getSymbol(name, SymType.FUNC)
        if (qry == null) {
            error(NotFound(location, name, SymType.FUNC))
            setType(ctx, Type.ERROR)
        } else {

            val f = qry as Function
            val args = f.args
            val exprs = ctx.exprList().expr()

            if (args.size > exprs.size) {
                error(ArgumentNumber(location, '-', name))
                setType(ctx, Type.ERROR)
            } else if (args.size < exprs.size) {
                error(ArgumentNumber(location, '+', name))
                setType(ctx, Type.ERROR)
            } else {
                for (i in args.indices) {
                    val argType = args[i].type
                    val exprType = getType(exprs[i])
                    val exp = exprs[i].text

                    if (argType == Type.ERROR || exprType == Type.ERROR) {
                        setType(ctx, Type.ERROR)
                        return
                    }

                    if (exprType != argType) {
                        error(Argument(location, exp, exprType, argType, name))
                        errorFound = true
                    }
                }

                setType(ctx, if (errorFound) Type.ERROR else f.type)
            }
        }
    }

    override fun exitFunctionCall(ctx: FunctionCallContext) {
        super.exitFunctionCall(ctx)
        setType(ctx, getType(ctx.funcCall()))
    }

    override fun exitCast(ctx: CastContext) {
        super.exitCast(ctx)

        val location = Location(ctx.type().start)
        val target = ctx.type().toGrpType()
        val current = getType(ctx.expr())

        if (current != Type.ERROR) {
            if (CastTable.check(target, current)) {
                setType(ctx, target)
            } else {
                error(CastError(location, target, current))
                setType(ctx, Type.ERROR)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    override fun exitAtomic(ctx: AtomicContext) {
        super.exitAtomic(ctx)
        setType(ctx, getType(ctx.atom()))
    }

    /**
     * Verifies the types used in an unary operation. For unary operators `+`
     * and `-` the expression type must be a numeric type. For unary operator
     * `!` the expression type must be `bool`
     */
    override fun exitUnary(ctx: UnaryContext) {
        super.exitUnary(ctx)

        val op = ctx.op.text
        val type = getType(ctx.expr())
        val location = Location(ctx.op)

        if (type != Type.ERROR) {
            val operationResult = OpTable.checkUnaryOp(op, type)

            if (operationResult == Type.ERROR) {
                error(BadUnaryOp(location, op, type))
                setType(ctx, Type.ERROR)
            } else {
                setType(ctx, operationResult)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Sets the type of an expression between parentheses to the same type of
     * the inner expression.
     */
    override fun exitAssoc(ctx: AssocContext) {
        super.exitAssoc(ctx)
        setType(ctx, getType(ctx.expr()))
    }

    /**
     * Verifies binary operations regarding operators `*`, `/` and `%`.
     */
    override fun exitMulDivMod(ctx: MulDivModContext) {
        super.exitMulDivMod(ctx)

        val lhs = getType(ctx.expr(0))
        val rhs = getType(ctx.expr(1))
        val op = ctx.op.text
        val location = Location(ctx.start)

        if (lhs != Type.ERROR && rhs != Type.ERROR) {
            val operationResult = OpTable.checkBinaryOp(op, lhs, rhs)

            if (operationResult == Type.ERROR) {
                error(BadBinaryOp(location, op, lhs, rhs))
                setType(ctx, Type.ERROR)
            } else {
                setType(ctx, operationResult)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Verifies binary operations regarding operators `+` and `-`.
     */
    override fun exitAddSub(ctx: AddSubContext) {
        super.exitAddSub(ctx)

        val lhs = getType(ctx.expr(0))
        val rhs = getType(ctx.expr(1))
        val op = ctx.op.text
        val location = Location(ctx.start)

        if (lhs != Type.ERROR && rhs != Type.ERROR) {
            val operationResult = OpTable.checkBinaryOp(op, lhs, rhs)

            if (operationResult == Type.ERROR) {
                error(BadBinaryOp(location, op, lhs, rhs))
                setType(ctx, Type.ERROR)
            } else {
                setType(ctx, operationResult)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Verifies binary operations regarding operators `>`, `<`, `<=` and `>=`.
     */
    override fun exitComparison(ctx: ComparisonContext) {
        super.exitComparison(ctx)

        val lhs = getType(ctx.expr(0))
        val rhs = getType(ctx.expr(1))
        val op = ctx.op.text
        val location = Location(ctx.start)

        if (lhs != Type.ERROR && rhs != Type.ERROR) {
            val operationResult = OpTable.checkBinaryOp(op, lhs, rhs)

            if (operationResult == Type.ERROR) {
                error(BadBinaryOp(location, op, lhs, rhs))
                setType(ctx, Type.ERROR)
            } else {
                setType(ctx, operationResult)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Verifies binary operations regarding operators `==` and `!=`.
     */
    override fun exitEquality(ctx: EqualityContext) {
        super.exitEquality(ctx)

        val type1 = getType(ctx.expr(0))
        val type2 = getType(ctx.expr(1))
        val op = ctx.op.text
        val location = Location(ctx.start)

        if (type1 != Type.ERROR && type2 != Type.ERROR) {
            val operationResult = OpTable.checkBinaryOp(op, type1, type2)

            if (operationResult == Type.ERROR) {
                error(BadBinaryOp(location, op, type1, type2))
                setType(ctx, Type.ERROR)
            } else {
                setType(ctx, operationResult)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Verifies binary operations regarding operator `&&`.
     */
    override fun exitLogicAnd(ctx: LogicAndContext) {
        super.exitLogicAnd(ctx)

        val lhs = getType(ctx.expr(0))
        val rhs = getType(ctx.expr(1))
        val location = Location(ctx.start)

        if (lhs != Type.ERROR && rhs != Type.ERROR) {
            val operationResult = OpTable.checkBinaryOp("&&", lhs, rhs)

            if (operationResult == Type.ERROR) {
                error(BadBinaryOp(location, "&&", lhs, rhs))
                setType(ctx, Type.ERROR)
            } else {
                setType(ctx, operationResult)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Verifies binary operations regarding operator `||`.
     */
    override fun exitLogicOr(ctx: LogicOrContext) {
        super.exitLogicOr(ctx)

        val lhs = getType(ctx.expr(0))
        val rhs = getType(ctx.expr(1))
        val location = Location(ctx.start)

        if (lhs != Type.ERROR && rhs != Type.ERROR) {
            val operationResult = OpTable.checkBinaryOp("||", lhs, rhs)

            if (operationResult == Type.ERROR) {
                error(BadBinaryOp(location, "||", lhs, rhs))
                setType(ctx, Type.ERROR)
            } else {
                setType(ctx, operationResult)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    override fun exitGlExpr(ctx: GlExprContext) {
        super.exitGlExpr(ctx)

        if (ctx.IntLit() != null) {
            val text = ctx.IntLit().text
            val location = Location(ctx.IntLit())

            try {
                ctx.IntLit().text.toLong()
                setType(ctx, Type.int)
            } catch (e: NumberFormatException) {
                error(OutOfRange(location, text))
                setType(ctx, Type.ERROR)
            }
        }

        if (ctx.FloatLit() != null) {
            val text = ctx.FloatLit().text
            val location = Location(ctx.FloatLit())

            try {
                ctx.FloatLit().text.toDouble()
                setType(ctx, Type.float)
            } catch (e: NumberFormatException) {
                error(OutOfRange(location, text))
                setType(ctx, Type.ERROR)
            }
        }

        if (ctx.BoolLit() != null) {
            setType(ctx, Type.bool)
        }

        if (ctx.CharLit() != null) {
            setType(ctx, Type.char)
        }

        if (ctx.StringLit() != null) {
            setType(ctx, Type.string)
        }
    }

    /**
     * Checks that the variable being declared is not declared already and that
     * if it is an assignment, check the type of the right hand value.
     */
    override fun exitVarDec(ctx: VarDecContext) {
        super.exitVarDec(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type().toGrpType()
        val location = Location(ctx.Identifier())
        val scope = scopeUID()
        val rhs = ctx.expr()

        if (rhs != null) {
            val rhsType = getType(rhs)
            val rhsText = rhs.text

            if (rhsType != Type.ERROR) {
                if (type != rhsType) {
                    error(BadAssignment(location, rhsText, type, rhsType))
                }
            }
        }

        val variable = Variable(name, type, scope, location)

        val qry = symTab.getSymbol(name, scope, SymType.VAR)
        when (qry) {
            null -> symTab.addSymbol(variable)
            else -> error(Redeclaration(location, variable.location, name, SymType.VAR))
        }
    }

    override fun exitAssignment(ctx: AssignmentContext) {
        super.exitAssignment(ctx)

        val lhs = ctx.expr(0)
        val rhs = ctx.expr(1)
        val lhsType = getType(lhs)
        val rhsType = getType(rhs)
        val location = Location(lhs.start)

        if (lhsType != Type.ERROR && rhsType != Type.ERROR) {
            if (!getAssignable(lhs)) {
                error(NonAssignable(location, lhs.text))
            } else {
                if (lhsType != rhsType) {
                    error(BadAssignment(location, rhs.text, lhsType, rhsType))
                }
            }
        }
    }

    override fun exitIfc(ctx: IfcContext) {
        super.exitIfc(ctx)

        val ifCond = ctx.expr().text
        val ifCondType = getType(ctx.expr())
        val ifCondLoc = Location(ctx.expr().start)

        if (ifCondType != Type.ERROR && ifCondType != Type.bool) {
            error(NonBoolCondition(ifCondLoc, ifCond, ifCondType))
        }

        for (s in ctx.elifc()) {
            val cond = s.expr().text
            val type = getType(s.expr())
            val loc = Location(s.expr().start)

            if (type != Type.ERROR && type != Type.bool) {
                error(NonBoolCondition(loc, cond, type))
            }
        }
    }

    override fun exitForc(ctx: ForcContext) {
        super.exitForc(ctx)

        if (ctx.cond != null) {
            val type = getType(ctx.cond)
            val location = Location(ctx.cond.start)
            val exp = ctx.cond.text

            if (type != Type.ERROR && type != Type.bool) {
                error(NonBoolCondition(location, exp, type))
            }
        }
    }

    override fun exitWhilec(ctx: WhilecContext) {
        super.exitWhilec(ctx)

        val type = getType(ctx.expr())
        val exp = ctx.expr().text
        val location = Location(ctx.expr().start)

        if (type != Type.ERROR && type != Type.bool) {
            error(NonBoolCondition(location, exp, type))
        }
    }
}
