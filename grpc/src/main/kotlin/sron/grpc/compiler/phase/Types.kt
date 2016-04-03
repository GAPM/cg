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

package sron.grpc.compiler.phase

import org.antlr.v4.runtime.ParserRuleContext
import sron.grpc.compiler.*
import sron.grpc.compiler.Annotation
import sron.grpc.compiler.internal.GrpParser.*
import sron.grpc.symbol.Function
import sron.grpc.symbol.Location
import sron.grpc.symbol.SymType
import sron.grpc.symbol.Variable
import sron.grpc.type.CastTable
import sron.grpc.type.IntTypes
import sron.grpc.type.OpTable
import sron.grpc.type.Type

class Types : Phase() {
    private var insideSimpleStmt = false

    /**
     * Retrieves the type of a (sub)parse tree
     *
     * @param ctx The parse tree
     * @return The type of the parse tree, `Type.error` if it does not exists
     */
    fun getType(ctx: ParserRuleContext) = annotations.get(ctx)?.type ?: Type.ERROR

    /**
     * Sets the type of a (sub)parse tree, if the parse tree does not have an
     * entry in the result map, it's created
     *
     * @param ctx The parse tree
     * @param type The context of the type to be assigned
     */
    fun setType(ctx: ParserRuleContext, type: Type) {
        val r = annotations.get(ctx) ?: Annotation()
        r.type = type
        annotations.put(ctx, r)
    }

    /**
     * Returns whether a (sub)parse tree correspond to an assignable expression.
     *
     * @param ctx The parse tree
     */
    fun getAssignable(ctx: ParserRuleContext) = annotations.get(ctx)?.assignable ?: false

    /**
     * Sets whether a (sub) parse tree correspond to an assignable expression.
     * If the parse tree  does not have an entry in the result map, it's
     * created.
     *
     * @param ctx The parse tree
     * @param v `true` if it's assignable, `false` otherwise
     */
    fun setAssignable(ctx: ParserRuleContext, v: Boolean) {
        var r = annotations.get(ctx) ?: Annotation()
        r.assignable = v
        annotations.put(ctx, r)
    }

    /**
     * Updates the scope whenever the phase enters a function definition.
     */
    override fun enterFuncDef(ctx: FuncDefContext) {
        super.enterFuncDef(ctx)
        val name = ctx.Identifier().text
        scope.push(name)
    }

    /**
     * Updates the scope to `"global"` whenever the phase leaves a function
     * definition.
     */
    override fun exitFuncDef(ctx: FuncDefContext) {
        super.exitFuncDef(ctx)
        scope.pop()
    }

    /**
     * Marks whenever the phase enters a simple statement. Variable declarations
     * inside simple statements are not global.
     */
    override fun enterSimpleStmt(ctx: SimpleStmtContext) {
        super.enterSimpleStmt(ctx)
        insideSimpleStmt = true
    }

    /**
     * Marks whenever the phase leaves a simple statement. Variable declarations
     * inside simple statements are not global.
     */
    override fun exitSimpleStmt(ctx: SimpleStmtContext) {
        super.exitSimpleStmt(ctx)
        insideSimpleStmt = false
    }

    /**
     * Sets the type of an global expression.
     */
    override fun exitGlExpr(ctx: GlExprContext) {
        super.exitGlExpr(ctx)

        if (ctx.IntLit() != null) {
            setType(ctx, Type.INTEGER_CONSTANT)
        }

        if (ctx.FloatLit() != null) {
            setType(ctx, Type.float)
        }

        if (ctx.DoubleLit() != null) {
            setType(ctx, Type.double)
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
     * Checks that the global variable being declared is not declared already
     * and that if it is an assignment, check the type of the right hand value.
     */
    override fun exitGlVarDec(ctx: GlVarDecContext) {
        super.exitGlVarDec(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type().toGrpType()
        val location = Location(ctx.Identifier())
        val scope = scopeUID()
        val rhs = ctx.glExpr()

        if (rhs != null) {
            val rhsType = getType(rhs)
            val rhsText = rhs.text

            if (rhsType != Type.ERROR) {
                if (rhsType == Type.INTEGER_CONSTANT) {
                    try {
                        val value = rhsText.toLong()
                        val realRhsType = IntTypes.getType(value)
                        if (!IntTypes.checkRange(value, type)) {
                            error(BadAssignment(location, rhsText, type, realRhsType))
                        }
                    } catch (e: NumberFormatException) {
                        error(IntegerOutOfRange(Location(rhs.IntLit()), rhsText))
                    }
                } else {
                    if (type != rhsType) {
                        error(BadAssignment(location, rhsText, type, rhsType))
                    }
                }
            }
        }

        val variable = Variable(name, type, scope, location)

        val qry = symTab.getSymbol(name, scope, SymType.VAR)
        when (qry) {
            null -> symTab.addSymbol(variable)
            else -> error(Redeclaration(location, qry.location, name, SymType.VAR))
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

    /**
     * Checks that a variable being used was declared already.
     */
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

    /**
     * Sets the type of an integer literal to the machine dependant integer
     * type.
     */
    override fun exitInteger(ctx: IntegerContext) {
        super.exitInteger(ctx)
        try {
            val value = ctx.IntLit().text.toLong()
            setType(ctx, IntTypes.getConstType(value))
        } catch (e: NumberFormatException) {
            error(IntegerOutOfRange(Location(ctx.IntLit()), ctx.IntLit().text))
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Sets the type of an float literal parse tree to the float type.
     */
    override fun exitFloat(ctx: FloatContext) {
        super.exitFloat(ctx)
        setType(ctx, Type.float)
    }

    /**
     * Sets the type of a double literal parse tree to the machine dependant
     * float type.
     */
    override fun exitDouble(ctx: DoubleContext) {
        super.exitDouble(ctx)
        setType(ctx, Type.double)
    }

    /**
     * Sets the type of a boolean literal parse tree to the bool type.
     */
    override fun exitBoolean(ctx: BooleanContext) {
        super.exitBoolean(ctx)
        setType(ctx, Type.bool)
    }

    /**
     * Sets the type of a character literal parse tree to the char type.
     */
    override fun exitCharacter(ctx: CharacterContext) {
        super.exitCharacter(ctx)
        setType(ctx, Type.char)
    }

    /**
     * Sets the type of a string literal parse tree to the string type.
     */
    override fun exitStringAtom(ctx: StringAtomContext) {
        super.exitStringAtom(ctx)
        setType(ctx, Type.string)
    }

    /**
     * Verifies that a function being called exists, that its being called with
     * the proper amount of arguments, that each argument passed to the function
     * have the required type and in the end, sets the type to the function
     * type.
     */
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

    /**
     * Verifies that a cast is valid and sets the type to the correspondent cast
     */
    override fun exitCast(ctx: CastContext) {
        super.exitCast(ctx)

        val type1 = ctx.type().toGrpType()
        val type2 = getType(ctx.expr())

        if (CastTable.check(type1, type2)) {
            setType(ctx, type1)
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Sets the type of an atomic value to the type of the corespondent atom
     */
    override fun exitAtomic(ctx: AtomicContext) {
        super.exitAtomic(ctx)
        annotations.put(ctx, annotations.get(ctx.atom()))
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
     * the expression.
     */
    override fun exitAssoc(ctx: AssocContext) {
        super.exitAssoc(ctx)
        annotations.put(ctx, annotations.get(ctx.expr()))
    }

    /**
     * Verifies binary operations regarding operators `*`, `/` and `%`.
     */
    override fun exitMulDivMod(ctx: MulDivModContext) {
        super.exitMulDivMod(ctx)

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
     * Verifies binary operations regarding operators `+` and `-`.
     */
    override fun exitAddSub(ctx: AddSubContext) {
        super.exitAddSub(ctx)

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
     * Verifies binary operations regarding operators `>`, `<`, `<=` and `>=`.
     */
    override fun exitComparison(ctx: ComparisonContext) {
        super.exitComparison(ctx)

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

        val type1 = getType(ctx.expr(0))
        val type2 = getType(ctx.expr(1))
        val location = Location(ctx.start)

        if (type1 != Type.ERROR && type2 != Type.ERROR) {
            val operationResult = OpTable.checkBinaryOp("&&", type1, type2)

            if (operationResult == Type.ERROR) {
                error(BadBinaryOp(location, "&&", type1, type2))
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

        val type1 = getType(ctx.expr(0))
        val type2 = getType(ctx.expr(1))
        val location = Location(ctx.start)

        if (type1 != Type.ERROR && type2 != Type.ERROR) {
            val operationResult = OpTable.checkBinaryOp("||", type1, type2)

            if (operationResult == Type.ERROR) {
                error(BadBinaryOp(location, "||", type1, type2))
                setType(ctx, Type.ERROR)
            } else {
                setType(ctx, operationResult)
            }
        } else {
            setType(ctx, Type.ERROR)
        }
    }

    /**
     * Checks that an expression is assignable and that there is not type
     * mismatch in the assignment.
     */
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

    /**
     * Pushes into scope an if block.
     */
    override fun enterIfc(ctx: IfcContext) {
        super.enterIfc(ctx)
        scope.push("if${nextId()}")
    }

    /**
     * Checks that conditions used in if and elif are of the type bool.
     */
    override fun exitIfc(ctx: IfcContext) {
        super.exitIfc(ctx)

        if (scope.lastElement().startsWith("if")) {
            scope.pop()
        }

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

    /**
     * Pops an if block from scope if any and pushes into scope an elif block.
     */
    override fun enterElifc(ctx: ElifcContext) {
        super.enterElifc(ctx)

        if (scope.lastElement().startsWith("if")) {
            scope.pop()
        }

        scope.push("elif${nextId()}")
    }

    /**
     * Pops an elif block out of scope.
     */
    override fun exitElifc(ctx: ElifcContext) {
        super.exitElifc(ctx)
        scope.pop()
    }

    /**
     * Pops an if block from scope if any and pushes into scope an else block.
     */
    override fun enterElsec(ctx: ElsecContext) {
        super.enterElsec(ctx)

        if (scope.lastElement().startsWith("if")) {
            scope.pop()
        }

        scope.push("else${nextId()}")
    }

    /**
     * Pops an else block out of scope.
     */
    override fun exitElsec(ctx: ElsecContext) {
        super.exitElsec(ctx)
        scope.pop()
    }

    /**
     * Pushes into scope a for loop block.
     */
    override fun enterForc(ctx: ForcContext) {
        super.enterForc(ctx)
        scope.push("for${nextId()}")
    }

    /**
     * Checks that a for condition is of the type bool and pops the for block
     * out of scope.
     */
    override fun exitForc(ctx: ForcContext) {
        super.exitForc(ctx)
        scope.pop()

        if (ctx.cond != null) {
            val type = getType(ctx.cond)
            val location = Location(ctx.cond.start)
            val exp = ctx.cond.text

            if (type != Type.ERROR && type != Type.bool) {
                error(NonBoolCondition(location, exp, type))
            }
        }
    }

    /**
     * Pushes into scope a while loop block.
     */
    override fun enterWhilec(ctx: WhilecContext) {
        super.enterWhilec(ctx)
        scope.push("while${nextId()}")
    }

    /**
     * Checks that a while condition is of the type bool and pops the while
     * block out of scope.
     */
    override fun exitWhilec(ctx: WhilecContext) {
        super.exitWhilec(ctx)
        scope.pop()

        val type = getType(ctx.expr())
        val exp = ctx.expr().text
        val location = Location(ctx.expr().start)

        if (type != Type.ERROR && type != Type.bool) {
            error(NonBoolCondition(location, exp, type))
        }
    }
}
