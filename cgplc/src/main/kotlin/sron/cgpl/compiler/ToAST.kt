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

package sron.cgpl.compiler

import org.antlr.v4.runtime.tree.ParseTreeProperty
import sron.cgpl.compiler.ast.*
import sron.cgpl.compiler.internal.CGPLBaseListener
import sron.cgpl.compiler.internal.CGPLParser.*
import sron.cgpl.type.Type
import sron.cgpl.type.toCGPLType
import java.util.*

class ToAST : CGPLBaseListener() {
    private val result = ParseTreeProperty<ASTNode>()
    private var initCtx: InitContext? = null

    fun getResult() = result.get(initCtx) as Init

    override fun exitInit(ctx: InitContext) {
        super.exitInit(ctx)
        initCtx = ctx

        val init = Init()

        for (funcDef in ctx.funcDef()) {
            init.funcDef.add(result.get(funcDef) as FuncDef)
        }

        for (glVarDec in ctx.glVarDec()) {
            init.glVarDec.add(result.get(glVarDec) as GlVarDec)
        }

        result.put(ctx, init)
    }

    override fun exitGlVarDec(ctx: GlVarDecContext) {
        super.exitGlVarDec(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type().toCGPLType()
        val expr = result.get(ctx.glExpr()) as GlExpr

        val glVarDec = GlVarDec(name, type, expr)
        result.put(ctx, glVarDec)
    }

    override fun exitGlExpr(ctx: GlExprContext) {
        super.exitGlExpr(ctx)

        val text = ctx.text
        var type = Type.ERROR

        if (ctx.BoolLit() != null) {
            type = Type.bool
        }

        if (ctx.CharLit() != null) {
            type = Type.char
        }

        if (ctx.IntLit() != null) {
            type = Type.int
        }

        if (ctx.FloatLit() != null) {
            type = Type.float
        }

        if (ctx.StringLit() != null) {
            type = Type.string
        }

        val glExpr = GlExpr(type, text)
        result.put(ctx, glExpr)
    }

    override fun exitFuncDef(ctx: FuncDefContext) {
        super.exitFuncDef(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type()?.toCGPLType() ?: Type.void
        val args = ArrayList<Arg>()

        ctx.argList().arg().forEach {
            val arg = result.get(it) as Arg
            args.add(arg)
        }

        val stmts = ArrayList<Stmt>()

        ctx.stmt().forEach {
            val stmt = result.get(it) as Stmt
            stmts.add(stmt)
        }

        val funcDef = FuncDef(name, type, args, stmts)
        result.put(ctx, funcDef)
    }

    override fun exitArg(ctx: ArgContext) {
        super.exitArg(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type().toCGPLType()

        val arg = Arg(name, type)
        result.put(ctx, arg)
    }

    override fun exitStmt(ctx: StmtContext) {
        super.exitStmt(ctx)
        var stmt: Stmt? = null

        ctx.simpleStmt()?.let { s ->
            s.varDec()?.let { vd ->
                stmt = result.get(vd) as VarDec
            }

            s.assignment()?.let { a ->
                stmt = result.get(a) as Assignment
            }

            s.expr()?.let { e ->
                stmt = result.get(e) as Expr
            }

            s.returnStmt()?.let { r ->
                stmt = result.get(r) as Return
            }
        }

        ctx.compoundStmt()?.let { s ->
            s.forc()?.let { f ->
                stmt = result.get(f) as For
            }

            s.ifc()?.let { i ->
                stmt = result.get(i) as If
            }

            s.whilec()?.let { w ->
                stmt = result.get(w) as While
            }
        }

        result.put(ctx, stmt!!)
    }

    override fun exitVarDec(ctx: VarDecContext) {
        super.exitVarDec(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type().toCGPLType()

        val varDec = VarDec(name, type)
        result.put(ctx, varDec)
    }

    override fun exitAssignment(ctx: AssignmentContext) {
        super.exitAssignment(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr

        var assign: Assignment

        if (ctx.op.text == "=") {
            assign = Assignment(lhs, rhs)
        } else {
            val op = when (ctx.op.text) {
                "+=" -> Operator.ADD
                "-=" -> Operator.SUB
                "*=" -> Operator.MUL
                "/=" -> Operator.DIV
                "%=" -> Operator.MOD
                "&&=" -> Operator.AND
                else -> Operator.OR
            }
            assign = Assignment(lhs, BinaryExp(op, lhs, rhs))
        }

        result.put(ctx, assign)
    }

    override fun exitAtomic(ctx: AtomicContext) {
        super.exitAtomic(ctx)

        result.put(ctx, result.get(ctx.atom()))
    }

    override fun exitInteger(ctx: IntegerContext) {
        super.exitInteger(ctx)

        val text = ctx.IntLit().text
        val literal = Literal(Type.int, text)

        result.put(ctx, literal)
    }

    override fun exitFloat(ctx: FloatContext) {
        super.exitFloat(ctx)

        val text = ctx.FloatLit().text
        val literal = Literal(Type.float, text)

        result.put(ctx, literal)
    }

    override fun exitBoolean(ctx: BooleanContext) {
        super.exitBoolean(ctx)

        val text = ctx.BoolLit().text
        val literal = Literal(Type.bool, text)

        result.put(ctx, literal)
    }

    override fun exitCharacter(ctx: CharacterContext) {
        super.exitCharacter(ctx)

        val text = ctx.CharLit().text
        val literal = Literal(Type.char, text)

        result.put(ctx, literal)
    }

    override fun exitString(ctx: StringContext) {
        super.exitString(ctx)

        val text = ctx.StringLit().text
        val literal = Literal(Type.string, text)

        result.put(ctx, literal)
    }

    override fun exitVarName(ctx: VarNameContext) {
        super.exitVarName(ctx)

        val name = ctx.Identifier().text
        val identifier = Identifier(name)

        result.put(ctx, identifier)
    }

    override fun exitFunctionCall(ctx: FunctionCallContext) {
        super.exitFunctionCall(ctx)

        val name = ctx.funcCall().Identifier().text
        val expr = ArrayList<Expr>()

        ctx.funcCall().exprList().expr().forEach {
            val exp = result.get(it) as Expr
            expr.add(exp)
        }

        val funcCall = FunctionCall(name, expr)
        result.put(ctx, funcCall)
    }

    override fun exitCast(ctx: CastContext) {
        super.exitCast(ctx)

        val type = ctx.type().toCGPLType()
        val expr = result.get(ctx.expr()) as Expr

        val cast = Cast(type, expr)
        result.put(ctx, cast)
    }

    override fun exitUnary(ctx: UnaryContext) {
        super.exitUnary(ctx)

        val expr = result.get(ctx.expr()) as Expr
        val op = if (ctx.op.text == "!") {
            Operator.NOT
        } else if (ctx.op.text == "-") {
            Operator.MINUS
        } else {
            Operator.PLUS
        }

        val unaryExpr = UnaryExpr(op, expr)
        result.put(ctx, unaryExpr)
    }

    override fun exitAssoc(ctx: AssocContext) {
        super.exitAssoc(ctx)

        result.put(ctx, result.get(ctx.expr()))
    }

    override fun exitMulDivMod(ctx: MulDivModContext) {
        super.exitMulDivMod(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val op = if (ctx.op.text == "*") {
            Operator.MUL
        } else if (ctx.op.text == "/") {
            Operator.DIV
        } else {
            Operator.MOD
        }

        val binaryExpr = BinaryExp(op, lhs, rhs)
        result.put(ctx, binaryExpr)
    }

    override fun exitAddSub(ctx: AddSubContext) {
        super.exitAddSub(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val op = if (ctx.op.text == "+") {
            Operator.ADD
        } else {
            Operator.SUB
        }

        val binaryExpr = BinaryExp(op, lhs, rhs)
        result.put(ctx, binaryExpr)
    }

    override fun exitComparison(ctx: ComparisonContext) {
        super.exitComparison(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val op = if (ctx.op.text == "<") {
            Operator.LESS
        } else if (ctx.op.text == "<=") {
            Operator.LESS_EQUAL
        } else if (ctx.op.text == ">") {
            Operator.HIGHER
        } else {
            Operator.HIGHER_EQUAL
        }

        val binaryExpr = BinaryExp(op, lhs, rhs)
        result.put(ctx, binaryExpr)
    }

    override fun exitEquality(ctx: EqualityContext) {
        super.exitEquality(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val op = if (ctx.op.text == "==") {
            Operator.EQUAL
        } else {
            Operator.NOT_EQUAL
        }

        val binaryExpr = BinaryExp(op, lhs, rhs)
        result.put(ctx, binaryExpr)
    }

    override fun exitLogicAnd(ctx: LogicAndContext) {
        super.exitLogicAnd(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val op = Operator.AND

        val binaryExpr = BinaryExp(op, lhs, rhs)
        result.put(ctx, binaryExpr)
    }

    override fun exitLogicOr(ctx: LogicOrContext) {
        super.exitLogicOr(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val op = Operator.OR

        val binaryExpr = BinaryExp(op, lhs, rhs)
        result.put(ctx, binaryExpr)
    }

    override fun exitReturnStmt(ctx: ReturnStmtContext) {
        super.exitReturnStmt(ctx)

        val expr = result.get(ctx.expr()) as Expr
        val ret = Return(expr)
        result.put(ctx, ret)
    }

    //TODO

    override fun exitIfc(ctx: IfcContext) {
        super.exitIfc(ctx)

        val cond = result.get(ctx.expr()) as Expr
        val stmts = ArrayList<Stmt>()
        val elifs = ArrayList<Elif>()
        val elsec = result.get(ctx.elsec()) as Else

        ctx.stmt().forEach {
            val stmt = result.get(it) as Stmt
            stmts.add(stmt)
        }

        ctx.elifc().forEach {
            val elifc = result.get(it) as Elif
            elifs.add(elifc)
        }

        val ifc = If(cond, stmts, elifs, elsec)
        result.put(ctx, ifc)
    }

    override fun exitElifc(ctx: ElifcContext) {
        super.exitElifc(ctx)

        val cond = result.get(ctx.expr()) as Expr
        val stmts = ArrayList<Stmt>()

        ctx.stmt().forEach {
            val stmt = result.get(it) as Stmt
            stmts.add(stmt)
        }

        val elif = Elif(cond, stmts)
        result.put(ctx, elif)
    }

    override fun exitElsec(ctx: ElsecContext) {
        super.exitElsec(ctx)

        val stmts = ArrayList<Stmt>()
        ctx.stmt().forEach {
            val stmt = result.get(it) as Stmt
            stmts.add(stmt)
        }

        val elsec = Else(stmts)
        result.put(ctx, elsec)
    }

    override fun exitContinue(ctx: ContinueContext) {
        super.exitContinue(ctx)
        val control = Control(StmtType.CONTINUE)
        result.put(ctx, control)
    }

    override fun exitBreak(ctx: BreakContext) {
        super.exitBreak(ctx)
        val control = Control(StmtType.BREAK)
        result.put(ctx, control)
    }

    override fun exitForc(ctx: ForcContext) {
        super.exitForc(ctx)

        val initial = result.get(ctx.initial) as Assignment
        val cond = result.get(ctx.cond) as Expr
        val mod = result.get(ctx.mod) as Assignment
        val stmts = ArrayList<Stmt>()

        ctx.loopStmt().forEach { ls ->
            ls.controlStmt()?.let {
                val control = result.get(it) as Control
                stmts.add(control)
            }

            ls.stmt()?.let {
                val stmt = result.get(it) as Stmt
                stmts.add(stmt)
            }
        }

        val forc = For(initial, cond, mod, stmts)
        result.put(ctx, forc)
    }

    override fun exitWhilec(ctx: WhilecContext) {
        super.exitWhilec(ctx)

        val cond = result.get(ctx.expr()) as Expr
        val stmts = ArrayList<Stmt>()

        ctx.loopStmt().forEach { ls ->
            ls.controlStmt()?.let {
                val control = result.get(it) as Control
                stmts.add(control)
            }

            ls.stmt()?.let {
                val stmt = result.get(it) as Stmt
                stmts.add(stmt)
            }
        }

        val whilec = While(cond, stmts)
        result.put(ctx, whilec)
    }
}
