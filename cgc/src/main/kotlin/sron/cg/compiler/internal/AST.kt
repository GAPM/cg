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

package sron.cg.compiler.internal

import org.antlr.v4.runtime.ParserRuleContext
import sron.cg.compiler.ast.*
import sron.cg.compiler.ast.Init
import sron.cg.compiler.internal.CGParser.*
import sron.cg.compiler.lang.*

/**
 * Converts a [TypeContext] to the equivalent [Type].
 */
private fun TypeContext.toCGType(): Type {
    if (primitiveType() != null) {
        val atomTypeStr = primitiveType().text
        return AtomType.valueOf(atomTypeStr)
    } else if (type() != null) {
        val innerType = type().toCGType()
        return ArrayType(innerType)
    } else {
        throw IllegalStateException()
    }
}

/**
 * Creates an instance of [Location] from a [ParserRuleContext].
 */
private fun ParserRuleContext.getLocation(): Location {
    val start = Point(start.line, start.charPositionInLine + 1)
    val stop = Point(stop.line, stop.charPositionInLine + stop.text.length)
    return Location(start, stop)
}

private fun LitContext.toASTNode(): Literal {
    var text = text
    val type = when (this) {
        is IntLitContext -> AtomType.int
        is FloatLitContext -> AtomType.float
        is BoolLitContext -> AtomType.bool
        is CharLitContext -> {
            text = text.substring(1, text.length - 1)
            AtomType.char
        }
        is StringLitContext -> {
            text = text.substring(1, text.length - 1)
            AtomType.string
        }
        else -> throw IllegalStateException()
    }
    return Literal(text, type, getLocation())
}

private fun GraphLitContext.toASTNode(): GraphLit {
    val type = when (gtype.type) {
        CGLexer.GRAPH -> GraphType.GRAPH
        CGLexer.DIGRAPH -> GraphType.DIGRAPH

        else -> throw IllegalStateException()
    }

    val edges = edge().map {
        Edge(it.source.toASTNode(), it.target.toASTNode())
    }

    return GraphLit(type, num.toASTNode(), edges, getLocation())
}

private fun FuncCallContext.toASTNode(): FunctionCall {
    val name = IDENTIFIER().text
    val expr = exprList().expr().map { it.toASTNode() }
    return FunctionCall(name, expr, getLocation())
}

private fun ArrayLitContext.toASTNode(): ArrayLit {
    val expr = exprList().expr().map { it.toASTNode() }
    return ArrayLit(expr, getLocation())
}

private fun AtomContext.toASTNode(): Atom = when (this) {
    is LiteralContext -> lit().toASTNode()
    is VarNameContext -> VarName(IDENTIFIER().text, getLocation())
    is GraphContext -> graphLit().toASTNode()
    is FunctionCallContext -> funcCall().toASTNode()
    is CastContext -> Cast(type().toCGType(), expr().toASTNode(),
            getLocation())
    is ArrayContext -> arrayLit().toASTNode()

    else -> throw IllegalStateException()
}

private fun ExprContext.toASTNode(): Expr = when (this) {
    is AtomicContext -> atom().toASTNode()
    is ArrayAccessContext ->
        ArrayAccess(array.toASTNode(), subscript.toASTNode(), getLocation())
    is UnaryContext ->
        UnaryExpr(Operator.fromToken(op), expr().toASTNode(), getLocation())
    is AssocContext -> expr().toASTNode()
    is MulDivModContext ->
        BinaryExpr(Operator.fromToken(op), expr(0).toASTNode(),
                expr(1).toASTNode(), getLocation())
    is AddSubContext ->
        BinaryExpr(Operator.fromToken(op), expr(0).toASTNode(),
                expr(1).toASTNode(), getLocation())
    is ComparisonContext ->
        BinaryExpr(Operator.fromToken(op), expr(0).toASTNode(),
                expr(1).toASTNode(), getLocation())
    is EqualityContext ->
        BinaryExpr(Operator.fromToken(op), expr(0).toASTNode(),
                expr(1).toASTNode(), getLocation())
    is LogicAndContext ->
        BinaryExpr(Operator.fromToken(op), expr(0).toASTNode(),
                expr(1).toASTNode(), getLocation())
    is LogicOrContext ->
        BinaryExpr(Operator.fromToken(op), expr(0).toASTNode(),
                expr(1).toASTNode(), getLocation())

    else -> throw IllegalStateException()
}

/**
 * Transforms a [VarDecContext] into a [VarDec] AST node.
 */
private fun VarDecContext.toASTNode(): VarDec {
    val name = IDENTIFIER().text
    val type = type()?.toCGType() ?: AtomType.UNKNOWN
    val init = expr()?.toASTNode() ?: null
    return VarDec(name, type, init, getLocation())
}

private fun AssignmentStmtContext.toASTNode(): Assignment {
    val lhs = lhs.toASTNode()
    val rhs = rhs.toASTNode()
    return Assignment(lhs, rhs, getLocation())
}

private fun ReturnStmtContext.toASTNode(): Return {
    val expr = expr().toASTNode()
    return Return(expr, getLocation())
}

private fun ControlStmtContext.toASTNode(): Control {
    val type = ControlType.fromToken(wr)
    return Control(type, getLocation())
}

private fun PrintStmtContext.toASTNode(): Print {
    val expr = expr().toASTNode()
    return Print(expr, getLocation())
}

private fun AssertStmtContext.toASTNode(): Assert {
    val expr = expr().toASTNode()
    return Assert(expr, getLocation())
}

private fun SimpleStmtContext.toASTNode(): Stmt {
    expr()?.let { return it.toASTNode() }
    assignmentStmt()?.let { return it.toASTNode() }
    varDec()?.let { return it.toASTNode() }
    returnStmt()?.let { return it.toASTNode() }
    controlStmt()?.let { return it.toASTNode() }
    printStmt()?.let { return it.toASTNode() }
    assertStmt()?.let { return it.toASTNode() }

    throw IllegalStateException()
}

private fun IfcContext.toASTNode(): IfBlock {
    val ifBody = stmt().map { it.toASTNode() }
    val elseBody = elsec().stmt().map { it.toASTNode() }
    val elif = elifc().map {
        val body = it.stmt().map { it.toASTNode() }
        Elif(it.expr().toASTNode(), body, it.getLocation())
    }
    val ifc = If(expr().toASTNode(), ifBody, getLocation())
    val elsec = Else(elseBody, getLocation())

    return IfBlock(ifc, elif, elsec, getLocation())
}

/**
 * Transforms a [ForcContext] into a [For] AST node.
 */
private fun ForcContext.toASTNode(): For {
    val initial = initial.toASTNode()
    val condition = expr().toASTNode()
    val modifier = mod.toASTNode()
    val body = stmt().map { it.toASTNode() }
    return For(initial, condition, modifier, body, getLocation())
}

/**
 * Transforms a [WhilecContext] into a [While] AST node.
 */
private fun WhilecContext.toASTNode(): While {
    val condition = expr().toASTNode()
    val body = stmt().map { it.toASTNode() }
    return While(condition, body, getLocation())
}

private fun CompoundStmtContext.toASTNode(): Stmt {
    ifc()?.let { return it.toASTNode() }
    forc()?.let { return it.toASTNode() }
    whilec()?.let { return it.toASTNode() }

    throw IllegalStateException()
}

/**
 * Transforms a [StmtContext] into a [Stmt] AST node.
 */
private fun StmtContext.toASTNode(): Stmt {
    simpleStmt()?.let { return it.toASTNode() }
    compoundStmt()?.let { return it.toASTNode() }

    throw IllegalStateException()
}

/**
 * Transforms a [ParamListContext] into a list of [Parameter] AST nodes.
 */
private fun ParamListContext.toASTNode(): List<Parameter> = param().map {
    Parameter(it.text, it.type().toCGType(), it.getLocation())
}

/**
 * Transforms a [FuncDefContext] into a [FuncDef] AST node.
 */
private fun FuncDefContext.toASTNode(): FuncDef {
    val name = IDENTIFIER().text
    val type = type()?.toCGType() ?: AtomType.void
    val params = paramList().toASTNode()
    val body = stmt().map { it.toASTNode() }
    return FuncDef(name, type, params, body, getLocation())
}

/**
 * Retrieves a full AST from a [UnitContext], which is the top level rule of
 * the CG grammar. The result is an instance of the class [Init], which is the
 * root node in every CG AST.
 */
fun ASTfromParseTree(init: InitContext): Init {
    val fds = init.funcDef().map { it.toASTNode() }
    val vds = init.varDec().map { it.toASTNode() }
    return Init(fds, vds, init.getLocation())
}
