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
import sron.grpc.compiler.Annotation
import sron.grpc.compiler.internal.GrpParser.*
import sron.grpc.compiler.nextId
import sron.grpc.compiler.toGrpType
import sron.grpc.symbol.Function
import sron.grpc.symbol.Location
import sron.grpc.symbol.SymType
import sron.grpc.symbol.Variable
import sron.grpc.type.*

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
     * Reports that a variable is being re-declared in a common scope
     *
     * @param last The re-declaration location
     * @param first The location of the already declared variable
     * @param name The name of the variable
     */
    fun redeclarationError(last: Location, first: Location, name: String) {
        val e = "redeclaration of variable `$name`. Previously declared at $first"
        addError(last, e)
    }

    /**
     * Reports that a function or variable that is not in the symbol table is
     * being used
     *
     * @param location the location of the use
     * @param name the name of the symbol
     * @param typ the symbol type
     */
    fun notFoundError(location: Location, name: String, typ: SymType) {
        val t = if (typ == SymType.FUNC) "function" else "variable"
        addError(location, "$t $name not found in current scope")
    }

    /**
     * Reports a type mismatch error while using an unary operator
     *
     * @param location The location of the error
     * @param op The operator
     * @param typ The type
     */
    fun unaryOpError(location: Location, op: String, typ: Type) {
        addError(location, "invalid unary operation: $op $typ")
    }

    /**
     * Reports a type mismatch error while using an binary operator
     *
     * @param location The location of the error
     * @param op The operator
     * @param typ1 The type of the left operand
     * @param typ2 The type of the right operand
     */
    fun binaryOpError(location: Location, op: String, typ1: Type, typ2: Type) {
        addError(location, "invalid binary operation: $typ1 $op $typ2")
    }

    /**
     * Reports a type mismatch in a function call regarding one of the
     * parameters
     *
     * @param location The location of the function call
     * @param expr The text of the expression with mismatched type
     * @param etype The type of the expression
     * @param atype The type expected by te argument
     * @param fName The function name
     */
    fun argumentError(location: Location, expr: String, etype: Type,
                      atype: Type, fName: String) {
        val msg = "can not use $expr (type $etype) as type $atype in argument to $fName"
        addError(location, msg)
    }

    /**
     * Reports an argument number mismatch in function call error
     *
     * @param location The location of the error
     * @param type The type of error: `+` for too many arguments, `-` for not
     *            enough arguments
     * @param fName The function name
     */
    fun argumentNumberError(location: Location, type: Char, fName: String) {
        val t = when (type) {
            '+' -> "too many arguments in call to $fName"
            else -> "not enough arguments in call to $fName"
        }

        addError(location, t)
    }

    /**
     * Reports that an assignment was attempted over a non-assignable
     * expression.
     *
     * @param location The location of the error
     * @param exp The non-assignable expression
     */
    fun nonAssignableError(location: Location, exp: String) {
        addError(location, "can not assign to $exp")
    }

    /**
     * Reports a type mismatch in an assignment.
     *
     * @param location The location of the error
     * @param exp The expression with a wrong type
     * @param type1 The expected type
     * @param type2 The received type
     */
    fun assignmentError(location: Location, exp: String, type1: Type,
                        type2: Type) {
        val m = "can not use $exp (type $type2) as type $type1 in assignment"
        addError(location, m)
    }

    /**
     * Reports that a condition is not of type bool.
     *
     * @param location The location of the error
     * @param exp The expression with a wrong type
     * @param type The type of the expression
     */
    fun conditionError(location: Location, exp: String, type: Type) {
        addError(location, "can not use $exp (type $type) as type bool in condition")
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
            val value = ctx.IntLit().text.toLong()
            setType(ctx, IntTypes.getType(value))
        }

        if (ctx.FloatLit() != null) {
            setType(ctx, Type.FLOAT)
        }

        if (ctx.DoubleLit() != null) {
            setType(ctx, Type.DOUBLE)
        }

        if (ctx.BoolLit() != null) {
            setType(ctx, Type.BOOL)
        }

        if (ctx.CharLit() != null) {
            setType(ctx, Type.CHAR)
        }

        if (ctx.StringLit() != null) {
            setType(ctx, Type.STRING)
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
        val expr = ctx.glExpr()

        if (expr != null) {
            val exprType = getType(expr)
            if (exprType != Type.ERROR && !(exprType equivalent type)) {
                assignmentError(location, expr.text, type, exprType)
                return
            }
        }

        val variable = Variable(name, type, scope, location)

        val qry = symTab.getSymbol(name, scope, SymType.VAR)
        when (qry) {
            null -> symTab.addSymbol(variable)
            else -> redeclarationError(location, qry.location, name)
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
        val expr = ctx.expr()
        val exprType = getType(expr)

        if (expr != null && exprType != Type.ERROR) {
            if (!(exprType equivalent type)) {
                assignmentError(location, expr.text, type, exprType)
                return
            }
        }

        val variable = Variable(name, type, scope, location)

        val qry = symTab.getSymbol(name, scope, SymType.VAR)
        when (qry) {
            null -> symTab.addSymbol(variable)
            else -> redeclarationError(location, variable.location, name)
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
                notFoundError(location, name, SymType.VAR)
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
        val value = ctx.IntLit().text.toLong()
        setType(ctx, IntTypes.getType(value))
    }

    /**
     * Sets the type of an float literal parse tree to the float type.
     */
    override fun exitFloat(ctx: FloatContext) {
        super.exitFloat(ctx)
        setType(ctx, Type.FLOAT)
    }

    /**
     * Sets the type of a double literal parse tree to the machine dependant
     * float type.
     */
    override fun exitDouble(ctx: DoubleContext) {
        super.exitDouble(ctx)
        setType(ctx, Type.DOUBLE)
    }

    /**
     * Sets the type of a boolean literal parse tree to the bool type.
     */
    override fun exitBoolean(ctx: BooleanContext) {
        super.exitBoolean(ctx)
        setType(ctx, Type.BOOL)
    }

    /**
     * Sets the type of a character literal parse tree to the char type.
     */
    override fun exitCharacter(ctx: CharacterContext) {
        super.exitCharacter(ctx)
        setType(ctx, Type.CHAR)
    }

    /**
     * Sets the type of a string literal parse tree to the string type.
     */
    override fun exitStringAtom(ctx: StringAtomContext) {
        super.exitStringAtom(ctx)
        setType(ctx, Type.STRING)
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
        var error = false

        val qry = symTab.getSymbol(name, SymType.FUNC)
        when (qry) {
            null -> {
                notFoundError(location, name, SymType.FUNC)
                setType(ctx, Type.ERROR)
            }
            else -> {
                val f = qry as Function
                val args = f.args
                val exprs = ctx.exprList().expr()

                if (args.size > exprs.size) {
                    argumentNumberError(location, '-', name)
                    setType(ctx, Type.ERROR)
                } else if (args.size < exprs.size) {
                    argumentNumberError(location, '+', name)
                    setType(ctx, Type.ERROR)
                } else {
                    for (i in args.indices) {
                        val typeArg = args[i].type
                        val typeExpr = getType(exprs[i])
                        val exp = exprs[i].text

                        if (typeArg == Type.ERROR || typeExpr == Type.ERROR) {
                            setType(ctx, Type.ERROR)
                            return
                        }

                        if (!(typeArg equivalent typeExpr)) {
                            argumentError(location, exp, typeExpr, typeArg, name)
                            error = true
                        }
                    }

                    setType(ctx, if (error) Type.ERROR else f.type)
                }
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
            val operationResult = OpTable.checkUnary(op, type)

            if (operationResult == Type.ERROR) {
                unaryOpError(location, op, type)
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
            val operationResult = OpTable.checkBinary(op, type1, type2)

            if (operationResult == Type.ERROR) {
                binaryOpError(location, op, type1, type2)
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
            val operationResult = OpTable.checkBinary(op, type1, type2)

            if (operationResult == Type.ERROR) {
                binaryOpError(location, op, type1, type2)
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
            val operationResult = OpTable.checkBinary(op, type1, type2)

            if (operationResult == Type.ERROR) {
                binaryOpError(location, op, type1, type2)
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
            val operationResult = OpTable.checkBinary(op, type1, type2)

            if (operationResult == Type.ERROR) {
                binaryOpError(location, op, type1, type2)
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
            val operationResult = OpTable.checkBinary("&&", type1, type2)

            if (operationResult == Type.ERROR) {
                binaryOpError(location, "&&", type1, type2)
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
            val operationResult = OpTable.checkBinary("||", type1, type2)

            if (operationResult == Type.ERROR) {
                binaryOpError(location, "||", type1, type2)
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

        val exp1 = ctx.expr(0)
        val exp2 = ctx.expr(1)
        val type1 = getType(exp1)
        val type2 = getType(exp2)
        val location = Location(exp1.start)

        if (type1 != Type.ERROR && type2 != Type.ERROR) {
            if (!getAssignable(exp1)) {
                nonAssignableError(location, exp1.text)
            } else {
                if (type1 != type2) {
                    assignmentError(location, exp2.text, type1, type2)
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

        if (ifCondType != Type.ERROR && ifCondType != Type.BOOL) {
            conditionError(ifCondLoc, ifCond, ifCondType)
        }

        for (s in ctx.elifc()) {
            val cond = s.expr().text
            val type = getType(s.expr())
            val loc = Location(s.expr().start)

            if (type != Type.ERROR && type != Type.BOOL) {
                conditionError(loc, cond, type)
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

            if (type != Type.ERROR && type != Type.BOOL) {
                conditionError(location, exp, type)
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

        if (type != Type.ERROR && type != Type.BOOL) {
            conditionError(location, exp, type)
        }
    }
}
