/*
 * Copyright 2016 Simón Oroño & La Universidad del Zulias
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

import sron.grpc.compiler.ControlNotInLoop
import sron.grpc.compiler.EmptyReturn
import sron.grpc.compiler.NonEmptyReturn
import sron.grpc.compiler.NotAllPathsReturn
import sron.grpc.compiler.internal.GrpParser.*
import sron.grpc.symbol.Location
import sron.grpc.type.Type
import sron.grpc.type.toGrpType

/**
 * [Structure] is the compilation phase that checks that all non-void functions
 * return a value, that void functions have only empty returns and that control
 * statements such as `continue` and `break` are used only inside loops.
 */
class Structure : Phase() {
    private var insideLoop = false
    private var insideFunction = false
    private var currentFunctionType = Type.ERROR
    private var fName = ""

    /**
     * Marks that the phase entered inside a loop (`for`).
     */
    override fun enterForc(ctx: ForcContext) {
        super.enterForc(ctx)
        insideLoop = true
    }

    /**
     * Marks that the phase entered inside a loop (`while`).
     */
    override fun enterWhilec(ctx: WhilecContext) {
        super.enterWhilec(ctx)
        insideLoop = true
    }

    /**
     * Marks that the phase leaved a loop (`for`).
     */
    override fun exitForc(ctx: ForcContext) {
        super.exitForc(ctx)
        insideLoop = false
    }

    /**
     * Marks that the phase leaved a loop (`while`).
     */
    override fun exitWhilec(ctx: WhilecContext) {
        super.exitWhilec(ctx)
        insideLoop = false
    }

    /**
     * Checks whenever the phase enters a `continue` statement that it is inside
     * a loop.
     */
    override fun enterContinue(ctx: ContinueContext) {
        super.enterContinue(ctx)
        if (!insideLoop) {
            error(ControlNotInLoop(Location(ctx.start), "continue"))
        }
    }

    /**
     * Checks whenever the phase enters a `break` statement that it is inside
     * a loop.
     */
    override fun enterBreak(ctx: BreakContext) {
        super.enterBreak(ctx)
        if (!insideLoop) {
            error(ControlNotInLoop(Location(ctx.start), "break"))
        }
    }

    /**
     * Marks that the phase entered a function definition, saving its name and
     * return type.
     */
    override fun enterFuncDef(ctx: FuncDefContext) {
        super.enterFuncDef(ctx)

        insideFunction = true
        currentFunctionType = ctx.type()?.toGrpType() ?: Type.void
        fName = ctx.Identifier().text
    }

    /**
     * Marks that the phase left a function definition, checking that all paths
     * returns a value (unless its type is `void`) and remove current name and
     * return type.
     */
    override fun exitFuncDef(ctx: FuncDefContext) {
        super.exitFuncDef(ctx)

        for (stmt in ctx.stmt()) {
            if (getReturns(stmt)) {
                setReturns(ctx, true)
                break
            }
        }

        if (!getReturns(ctx) && currentFunctionType != Type.void && fName != "main") {
            error(NotAllPathsReturn(Location(ctx.Identifier()), fName))
        }

        insideFunction = false
        currentFunctionType = Type.ERROR
        fName = ""
    }

    /**
     * Checks whenever the phase leaves an if statement that all branches have a
     * return statement.
     */
    override fun exitIfc(ctx: IfcContext) {
        super.exitIfc(ctx)

        if (ctx.elsec() == null) {
            setReturns(ctx, false)
            return
        }

        var mainIfReturns = false
        var allElifReturns = true
        var elseReturns = false

        val ifStmts = ctx.stmt() // statements inside `if` block
        val elifs = ctx.elifc() // `else if` blocks
        val elseStmts = ctx.elsec().stmt() // statements inside `else` block

        for (s in ifStmts) {
            if (getReturns(s)) {
                mainIfReturns = true
                break
            }
        }

        for (e in elifs) {
            var thisElifReturns = false
            val elifStmts = e.stmt()

            for (s in elifStmts) {
                if (getReturns(s)) {
                    thisElifReturns = true
                    break
                }
            }

            allElifReturns = allElifReturns && thisElifReturns
        }

        for (s in elseStmts) {
            if (getReturns(s)) {
                elseReturns = true
                break
            }
        }

        setReturns(ctx, mainIfReturns && allElifReturns && elseReturns)
    }

    /**
     * Marks that a return statement is indeed a return statement (used to check
     * that all paths in a function have a return) and reports if a void
     * function has non-empty return or a non-void function has empty return.
     */
    override fun enterReturn(ctx: ReturnContext) {
        super.enterReturn(ctx)
        setReturns(ctx, true)
        val location = Location(ctx.start)

        if (currentFunctionType == Type.void) {
            if (ctx.expr() != null) {
                error(NonEmptyReturn(location, fName))
            }
        } else {
            if (ctx.expr() == null) {
                error(EmptyReturn(location, fName))
            }
        }
    }

    /**
     * Sets the result of a simple statement to the same result of its used
     * child.
     */
    override fun exitSimpleStmt(ctx: SimpleStmtContext) {
        super.exitSimpleStmt(ctx)

        ctx.varDec()?.let {
            annotations.put(ctx, annotations.get(it))
        }

        ctx.assignment()?.let {
            annotations.put(ctx, annotations.get(it))
        }

        ctx.controlStmt()?.let {
            annotations.put(ctx, annotations.get(it))
        }

        ctx.expr()?.let {
            annotations.put(ctx, annotations.get(it))
        }
    }

    /**
     * Sets the result of a compound statement to the same result of its used
     * child.
     */
    override fun exitCompoundStmt(ctx: CompoundStmtContext) {
        super.exitCompoundStmt(ctx)

        ctx.ifc()?.let {
            annotations.put(ctx, annotations.get(it))
        }

        ctx.forc()?.let {
            annotations.put(ctx, annotations.get(it))
        }

        ctx.whilec()?.let {
            annotations.put(ctx, annotations.get(it))
        }
    }

    /**
     * Sets the result of a statement to the same result of its used child.
     */
    override fun exitStmt(ctx: StmtContext) {
        super.exitStmt(ctx)

        ctx.simpleStmt()?.let {
            annotations.put(ctx, annotations.get(it))
        }

        ctx.compoundStmt()?.let {
            annotations.put(ctx, annotations.get(it))
        }
    }
}
