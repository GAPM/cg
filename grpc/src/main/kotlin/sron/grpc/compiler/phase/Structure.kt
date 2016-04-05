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

import sron.grpc.compiler.EmptyReturn
import sron.grpc.compiler.NonEmptyReturn
import sron.grpc.compiler.NotAllPathsReturn
import sron.grpc.compiler.internal.GrpParser.*
import sron.grpc.symbol.Location
import sron.grpc.type.Type
import sron.grpc.type.toGrpType

/**
 * [Structure] is the compilation phase that checks that all non-void functions
 * return a value and that void functions have only empty returns.
 */
class Structure : Phase() {
    private var insideFunction = false
    private var currentFunctionType = Type.ERROR
    private var currentFunctionName = ""

    override fun enterFuncDef(ctx: FuncDefContext) {
        super.enterFuncDef(ctx)

        insideFunction = true
        currentFunctionType = ctx.type()?.toGrpType() ?: Type.void
        currentFunctionName = ctx.Identifier().text
    }

    override fun exitFuncDef(ctx: FuncDefContext) {
        super.exitFuncDef(ctx)

        for (stmt in ctx.stmt()) {
            if (getReturns(stmt)) {
                setReturns(ctx, true)
                break
            }
        }

        if (!getReturns(ctx) && currentFunctionType != Type.void && currentFunctionName != "main") {
            error(NotAllPathsReturn(Location(ctx.Identifier()), currentFunctionName))
        }

        insideFunction = false
        currentFunctionType = Type.ERROR
        currentFunctionName = ""
    }

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

    override fun exitReturnStmt(ctx: ReturnStmtContext) {
        super.exitReturnStmt(ctx)
        setReturns(ctx, true)
        val location = Location(ctx.start)

        if (currentFunctionType == Type.void && ctx.expr() != null) {
            error(NonEmptyReturn(location, currentFunctionName))
        }

        if (currentFunctionType != Type.void && ctx.expr() == null) {
            error(EmptyReturn(location, currentFunctionName))
        }
    }

    override fun exitSimpleStmt(ctx: SimpleStmtContext) {
        super.exitSimpleStmt(ctx)

        if (ctx.returnStmt() != null) {
            setReturns(ctx, true)
        } else {
            setReturns(ctx, false)
        }
    }

    override fun exitCompoundStmt(ctx: CompoundStmtContext) {
        super.exitCompoundStmt(ctx)

        // Only the if statement can be guaranteed to return or not, so loop
        // are ignored
        ctx.ifc()?.let {
            setReturns(ctx, getReturns(it))
        }
    }

    override fun exitStmt(ctx: StmtContext) {
        super.exitStmt(ctx)

        ctx.simpleStmt()?.let {
            setReturns(ctx, getReturns(it))
        }

        ctx.compoundStmt()?.let {
            setReturns(ctx, getReturns(it))
        }
    }
}
