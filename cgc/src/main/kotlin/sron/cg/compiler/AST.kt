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

package sron.cg.compiler

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.antlr.v4.runtime.tree.ParseTreeWalker
import sron.cg.compiler.ast.*
import sron.cg.compiler.internal.CGBaseListener
import sron.cg.compiler.internal.CGLexer
import sron.cg.compiler.internal.CGParser.*
import sron.cg.compiler.symbol.Location
import sron.cg.compiler.type.Type
import sron.cg.compiler.type.toCGType
import java.util.*

object AST : CGBaseListener() {
    private val result = ParseTreeProperty<ASTNode>()
    private var initCtx: InitContext? = null

    operator fun invoke(tree: ParseTree): Init {
        val walker = ParseTreeWalker()
        walker.walk(this, tree)
        return result.get(initCtx) as Init
    }

    override fun exitInit(ctx: InitContext) {
        super.exitInit(ctx)
        initCtx = ctx

        val init = Init()

        for (funcDef in ctx.funcDef()) {
            val fd = result.get(funcDef) as FuncDef
            init.funcDef.add(fd)
        }

        for (glVarDec in ctx.glVarDec()) {
            val gvd = result.get(glVarDec) as GlVarDec
            init.glVarDec.add(gvd)
        }

        result.put(ctx, init)
    }

    override fun exitGlVarDec(ctx: GlVarDecContext) {
        super.exitGlVarDec(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type().toCGType()
        var expr: GlExpr? = null
        val location = Location(ctx.Identifier())

        if (ctx.glExpr() != null) {
            expr = result.get(ctx.glExpr()) as GlExpr
        }

        val glVarDec = GlVarDec(name, type, expr, location)

        result.put(ctx, glVarDec)
    }

    override fun exitGlExpr(ctx: GlExprContext) {
        super.exitGlExpr(ctx)

        val text = ctx.text
        var type = Type.ERROR
        val location = Location(ctx.start)

        if (ctx.BoolLit() != null) {
            type = Type.bool
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

        val glExpr = GlExpr(type, text, location)
        result.put(ctx, glExpr)
    }

    override fun exitFuncDef(ctx: FuncDefContext) {
        super.exitFuncDef(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type()?.toCGType() ?: Type.void
        val args = ArrayList<Arg>()
        val stmts = ArrayList<Stmt>()
        val location = Location(ctx.Identifier())

        for (a in ctx.argList().arg()) {
            val arg = result.get(a) as Arg
            args.add(arg)
        }

        for (s in ctx.stmt()) {
            val stmt = result.get(s) as Stmt
            stmts.add(stmt)
        }

        val funcDef = FuncDef(name, type, args, stmts, location)

        result.put(ctx, funcDef)
    }

    override fun exitArg(ctx: ArgContext) {
        super.exitArg(ctx)

        val name = ctx.Identifier().text
        val type = ctx.type().toCGType()
        val location = Location(ctx.Identifier())

        val arg = Arg(name, type, location)
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

            s.controlStmt()?.let { c ->
                stmt = result.get(c) as Control
            }

            s.printStmt()?.let { p ->
                stmt = result.get(p) as Print
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
        val type = ctx.type().toCGType()
        var expr: Expr? = null
        val location = Location(ctx.Identifier())

        if (ctx.expr() != null) {
            expr = result.get(ctx.expr()) as Expr
        }

        val varDec = VarDec(name, type, expr, location)

        result.put(ctx, varDec)
    }

    override fun exitAssignment(ctx: AssignmentContext) {
        super.exitAssignment(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val location = Location(ctx.start)

        val assign: Assignment

        if (ctx.op.type == CGLexer.EQUAL) {
            assign = Assignment(lhs, rhs, location)
        } else {
            val op = when (ctx.op.type) {
                CGLexer.ADD_ASSIGN -> Operator.ADD
                CGLexer.SUB_ASSIGN -> Operator.SUB
                CGLexer.MUL_ASSIGN -> Operator.MUL
                CGLexer.DIV_ASSIGN -> Operator.DIV
                CGLexer.MOD_ASSIGN -> Operator.MOD
                CGLexer.AND_ASSIGN -> Operator.AND
                else -> Operator.OR
            }
            assign = Assignment(lhs, BinaryExpr(op, lhs, rhs, location), location)
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
        val location = Location(ctx.start)
        val literal = Literal(Type.int, text, location)

        result.put(ctx, literal)
    }

    override fun exitFloat(ctx: FloatContext) {
        super.exitFloat(ctx)

        val text = ctx.FloatLit().text
        val location = Location(ctx.start)
        val literal = Literal(Type.float, text, location)

        result.put(ctx, literal)
    }

    override fun exitBoolean(ctx: BooleanContext) {
        super.exitBoolean(ctx)

        val text = ctx.BoolLit().text
        val location = Location(ctx.start)
        val literal = Literal(Type.bool, text, location)

        result.put(ctx, literal)
    }

    override fun exitString(ctx: StringContext) {
        super.exitString(ctx)

        val text = ctx.StringLit().text
        val location = Location(ctx.start)
        val literal = Literal(Type.string, text, location)

        result.put(ctx, literal)
    }

    override fun exitVarName(ctx: VarNameContext) {
        super.exitVarName(ctx)

        val name = ctx.Identifier().text
        val location = Location(ctx.Identifier())
        val identifier = Identifier(name, location)

        result.put(ctx, identifier)
    }

    override fun exitGraph(ctx: GraphContext) {
        super.exitGraph(ctx)

        val graphLit = ctx.graphLit()
        val gEdges = graphLit.edge()
        val location = Location(ctx.start)
        val num = graphLit.num

        val type = when (graphLit.gtype.type) {
            CGLexer.GRAPH -> GraphType.GRAPH
            else -> GraphType.DIGRAPH
        }

        val numExp = result.get(num) as Expr

        val edges = Array(gEdges.size) { i ->
            val edge = gEdges[i]
            val edgeLocation = Location(edge.start)
            val sourceExp = result.get(edge.source) as Expr
            val targetExp = result.get(edge.target) as Expr
            Edge(sourceExp, targetExp, edgeLocation)
        }

        val graph = Graph(type, numExp, edges, location)
        result.put(ctx, graph)
    }

    override fun exitFunctionCall(ctx: FunctionCallContext) {
        super.exitFunctionCall(ctx)

        val name = ctx.funcCall().Identifier().text
        val expr = ArrayList<Expr>()
        val location = Location(ctx.funcCall().Identifier())

        for (e in ctx.funcCall().exprList().expr()) {
            val exp = result.get(e) as Expr
            expr.add(exp)
        }

        val funcCall = FunctionCall(name, expr, location)
        result.put(ctx, funcCall)
    }

    override fun exitCast(ctx: CastContext) {
        super.exitCast(ctx)

        val type = ctx.type().toCGType()
        val expr = result.get(ctx.expr()) as Expr
        val location = Location(ctx.start)

        val cast = Cast(type, expr, location)
        result.put(ctx, cast)
    }

    override fun exitUnary(ctx: UnaryContext) {
        super.exitUnary(ctx)

        val expr = result.get(ctx.expr()) as Expr
        val op = if (ctx.op.type == CGLexer.NOT) {
            Operator.NOT
        } else if (ctx.op.type == CGLexer.SUB) {
            Operator.MINUS
        } else {
            Operator.PLUS
        }

        val location = Location(ctx.start)

        val unaryExpr = UnaryExpr(op, expr, location)
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
        val location = Location(ctx.start)
        val op = if (ctx.op.type == CGLexer.MUL) {
            Operator.MUL
        } else if (ctx.op.type == CGLexer.DIV) {
            Operator.DIV
        } else {
            Operator.MOD
        }

        val binaryExpr = BinaryExpr(op, lhs, rhs, location)
        result.put(ctx, binaryExpr)
    }

    override fun exitAddSub(ctx: AddSubContext) {
        super.exitAddSub(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val location = Location(ctx.start)

        val op = if (ctx.op.type == CGLexer.ADD) {
            Operator.ADD
        } else {
            Operator.SUB
        }

        val binaryExpr = BinaryExpr(op, lhs, rhs, location)
        result.put(ctx, binaryExpr)
    }

    override fun exitComparison(ctx: ComparisonContext) {
        super.exitComparison(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val location = Location(ctx.start)

        val op = if (ctx.op.type == CGLexer.LT) {
            Operator.LESS
        } else if (ctx.op.type == CGLexer.LE) {
            Operator.LESS_EQUAL
        } else if (ctx.op.type == CGLexer.GT) {
            Operator.GREATER
        } else {
            Operator.GREATER_EQUAL
        }

        val binaryExpr = BinaryExpr(op, lhs, rhs, location)
        result.put(ctx, binaryExpr)
    }

    override fun exitEquality(ctx: EqualityContext) {
        super.exitEquality(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val location = Location(ctx.start)

        val op = if (ctx.op.type == CGLexer.EQUAL_EQUAL) {
            Operator.EQUAL
        } else {
            Operator.NOT_EQUAL
        }

        val binaryExpr = BinaryExpr(op, lhs, rhs, location)
        result.put(ctx, binaryExpr)
    }

    override fun exitLogicAnd(ctx: LogicAndContext) {
        super.exitLogicAnd(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val op = Operator.AND
        val location = Location(ctx.start)

        val binaryExpr = BinaryExpr(op, lhs, rhs, location)
        result.put(ctx, binaryExpr)
    }

    override fun exitLogicOr(ctx: LogicOrContext) {
        super.exitLogicOr(ctx)

        val lhs = result.get(ctx.expr(0)) as Expr
        val rhs = result.get(ctx.expr(1)) as Expr
        val op = Operator.OR
        val location = Location(ctx.start)

        val binaryExpr = BinaryExpr(op, lhs, rhs, location)
        result.put(ctx, binaryExpr)
    }

    override fun exitReturnStmt(ctx: ReturnStmtContext) {
        super.exitReturnStmt(ctx)
        var expr: Expr? = null

        if (ctx.expr() != null) {
            expr = result.get(ctx.expr()) as Expr
        }

        val location = Location(ctx.start)
        val ret = Return(expr, location)
        result.put(ctx, ret)
    }

    override fun exitPrintStmt(ctx: PrintStmtContext) {
        super.exitPrintStmt(ctx)

        val location = Location(ctx.start)
        val expr = result.get(ctx.expr()) as Expr

        val print = Print(location, expr)
        result.put(ctx, print)
    }

    override fun exitIfc(ctx: IfcContext) {
        super.exitIfc(ctx)

        val cond = result.get(ctx.expr()) as Expr
        val stmts = ArrayList<Stmt>()
        val elifs = ArrayList<Elif>()
        var elsec: Else? = null
        val location = Location(ctx.start)

        ctx.elsec()?.let {
            elsec = result.get(ctx.elsec()) as Else
        }

        ctx.stmt().forEach {
            val stmt = result.get(it) as Stmt
            stmts.add(stmt)
        }

        ctx.elifc().forEach {
            val elifc = result.get(it) as Elif
            elifs.add(elifc)
        }

        val ifc = If(cond, stmts, elifs, elsec, location)
        result.put(ctx, ifc)
    }

    override fun exitElifc(ctx: ElifcContext) {
        super.exitElifc(ctx)

        val cond = result.get(ctx.expr()) as Expr
        val stmts = ArrayList<Stmt>()
        val location = Location(ctx.start)

        ctx.stmt().forEach {
            val stmt = result.get(it) as Stmt
            stmts.add(stmt)
        }

        val elif = Elif(cond, stmts, location)
        result.put(ctx, elif)
    }

    override fun exitElsec(ctx: ElsecContext) {
        super.exitElsec(ctx)

        val stmts = ArrayList<Stmt>()
        ctx.stmt().forEach {
            val stmt = result.get(it) as Stmt
            stmts.add(stmt)
        }
        val location = Location(ctx.start)

        val elsec = Else(stmts, location)
        result.put(ctx, elsec)
    }

    override fun exitControlStmt(ctx: ControlStmtContext) {
        super.exitControlStmt(ctx)
        val location = Location(ctx.start)
        val type = if (ctx.wr.type == CGLexer.CONTINUE) {
            ControlType.CONTINUE
        } else {
            ControlType.BREAK
        }

        val control = Control(type, location)

        result.put(ctx, control)
    }

    override fun exitForc(ctx: ForcContext) {
        super.exitForc(ctx)

        val initial = result.get(ctx.initial) as Assignment
        val cond = result.get(ctx.cond) as Expr
        val mod = result.get(ctx.mod) as Assignment
        val stmts = ArrayList<Stmt>()
        val location = Location(ctx.start)

        for (stmt in ctx.stmt()) {
            val st = result.get(stmt) as Stmt
            stmts.add(st)
        }

        val forc = For(initial, cond, mod, stmts, location)
        result.put(ctx, forc)
    }

    override fun exitWhilec(ctx: WhilecContext) {
        super.exitWhilec(ctx)

        val cond = result.get(ctx.expr()) as Expr
        val stmts = ArrayList<Stmt>()
        val location = Location(ctx.start)

        for (stmt in ctx.stmt()) {
            val st = result.get(stmt) as Stmt
            stmts.add(st)
        }

        val whilec = While(cond, stmts, location)
        result.put(ctx, whilec)
    }
}
