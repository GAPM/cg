package grpc.lib.compiler.phase

import grpc.lib.compiler.UnitResult
import grpc.lib.compiler.internal.GrpParser.*
import grpc.lib.compiler.tokIdxToType
import grpc.lib.symbol.Location
import grpc.lib.symbol.Type
import org.antlr.v4.runtime.ParserRuleContext

/**
 * [Structure] is the compilation phase that checks that all non-void functions
 * return a value, that void functions have only empty returns and that control
 * statements such as `continue` and `break` are used only inside loops.
 */
class Structure : Phase() {
    private var insideLoop = false
    private var insideFunction = false
    private var currentFunctionType = Type.none
    private var fName = ""

    /**
     *
     */
    private fun setReturns(ctx: ParserRuleContext, v: Boolean) {
        var r = results!!.get(ctx)

        if (r == null) {
            r = UnitResult()
        }

        r.returns = v
        results!!.put(ctx, r)
    }

    /**
     *
     */
    private fun getReturns(ctx: ParserRuleContext): Boolean {
        val r = results!!.get(ctx)
        return r?.returns ?: false
    }

    /**
     * Reports that `continue` or `break` were used outside a loop
     *
     * @param location The location of the error
     * @param word `continue` or `break`
     */
    private fun controlStmtError(location: Location, word: String) {
        addError(location, "`$word` not inside a loop")
    }

    /**
     * Reports that a non-empty return is inside a `void` function
     *
     * @param location The location of the error
     */
    private fun nonEmptyReturnError(location: Location) {
        addError(location, "non-empty return in void function `$fName`")
    }

    /**
     * Reports that a non-void function doesn't guarantee a return
     *
     * @param location The location of the function
     */
    private fun notAllPathsReturnError(location: Location) {
        addError(location, "in function $fName: not all paths have a return")
    }

    /**
     * Marks that the phase entered inside a loop (`for`)
     *
     * @param ctx The context of the `for` loop
     */
    override fun enterForc(ctx: ForcContext) {
        super.enterForc(ctx)
        insideLoop = true
    }

    /**
     * Marks that the phase entered inside a loop (`while`)
     *
     * @param ctx The context of the `while` loop
     */
    override fun enterWhilec(ctx: WhilecContext) {
        super.enterWhilec(ctx)
        insideLoop = true
    }

    /**
     * Marks that the phase leaved a loop (`for`)
     *
     * @param ctx The context of the `for` loop
     */
    override fun exitForc(ctx: ForcContext) {
        super.exitForc(ctx)
        insideLoop = false
    }

    /**
     * Marks that the phase leaved a loop (`while`)
     *
     * @param ctx The context of the `while` loop
     */
    override fun exitWhilec(ctx: WhilecContext) {
        super.exitWhilec(ctx)
        insideLoop = false
    }

    /**
     * Checks whenever the phase enters a `continue` statement that it is inside
     * a loop
     *
     * @param ctx The `continue` statement context
     */
    override fun enterContinue(ctx: ContinueContext) {
        super.enterContinue(ctx)
        if (!insideLoop) {
            controlStmtError(Location(ctx.start), "continue")
        }
    }

    /**
     * Checks whenever the phase enters a `break` statement that it is inside
     * a loop
     *
     * @param ctx The `break` statement context
     */
    override fun enterBreak(ctx: BreakContext) {
        super.enterBreak(ctx)
        if (!insideLoop) {
            controlStmtError(Location(ctx.start), "break")
        }
    }

    /**
     * Marks that the phase entered a function definition, saving its name and
     * return type
     *
     * @param ctx The context of the function definition
     */
    override fun enterFdef(ctx: FdefContext) {
        super.enterFdef(ctx)

        insideFunction = true
        currentFunctionType = tokIdxToType(ctx.typ())
        fName = ctx.Identifier().text
    }

    /**
     * Marks that the phase leaved a function definition, checking that all paths
     * returns a value (unless its type is `void`) and removing current name and
     * return type
     *
     * @param ctx The context of the function definition
     */
    override fun exitFdef(ctx: FdefContext) {
        super.exitFdef(ctx)

        for (s in ctx.stmt()) {
            if (getReturns(s)) {
                setReturns(ctx, true)
            }
        }

        if (!getReturns(ctx) && currentFunctionType != Type.void && fName != "main") {
            notAllPathsReturnError(Location(ctx.Identifier()))
        }

        insideFunction = false
        currentFunctionType = Type.none
        fName = ""
    }

    /**
     * Checks whenever the phase leaves an if statement, if all branches have a
     * return statement.
     *
     * @param ctx The context of the if statement
     */
    override fun exitIfc(ctx: IfcContext) {
        super.exitIfc(ctx)

        var mainIfReturns = false
        var allElifReturns = true
        var elseReturns = false

        val ifStmts = ctx.stmt()
        val elifs = ctx.elifc()

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

        if (ctx.elsec() != null) {
            val elseStmts = ctx.elsec().stmt()
            for (s in elseStmts) {
                if (getReturns(s)) {
                    elseReturns = true
                    break
                }
            }
        }

        setReturns(ctx, mainIfReturns && allElifReturns && elseReturns)
    }

    /**
     * Marks that a return statement is indeed a return statement (used to check
     * that all paths in a function have a return) and checks if a void function
     * has empty returns.
     *
     * @param ctx The context of the return statement
     */
    override fun enterReturn(ctx: ReturnContext) {
        super.enterReturn(ctx)
        setReturns(ctx, true)

        if (currentFunctionType == Type.void && ctx.expr() != null) {
            nonEmptyReturnError(Location(ctx.start))
        }
    }

    /**
     * Sets the result of a simple statement to the same result of its used child
     *
     * @param ctx The context of the simple statement
     */
    override fun exitSimpleStmt(ctx: SimpleStmtContext) {
        super.exitSimpleStmt(ctx)

        if (ctx.vdec() != null) {
            results?.put(ctx, results?.get(ctx.vdec()))
        }

        if (ctx.assign() != null) {
            results?.put(ctx, results?.get(ctx.assign()))
        }

        if (ctx.controlStmt() != null) {
            results?.put(ctx, results?.get(ctx.controlStmt()))
        }

        if (ctx.expr() != null) {
            results?.put(ctx, results?.get(ctx.expr()))
        }
    }

    /**
     * Sets the result of a compound statement to the same result of its used
     * child
     *
     * @param ctx The context of the compound statement
     */
    override fun exitCompoundStmt(ctx: CompoundStmtContext) {
        super.exitCompoundStmt(ctx)

        if (ctx.ifc() != null) {
            results?.put(ctx, results?.get(ctx.ifc()))
        }

        if (ctx.forc() != null) {
            results?.put(ctx, results?.get(ctx.forc()))
        }

        if (ctx.whilec() != null) {
            results?.put(ctx, results?.get(ctx.whilec()))
        }
    }

    /**
     * Sets the result of a statement to the same result of its used child
     *
     * @param ctx The context of the statement
     */
    override fun exitStmt(ctx: StmtContext) {
        super.exitStmt(ctx)

        if (ctx.simpleStmt() != null) {
            results?.put(ctx, results?.get(ctx.simpleStmt()))
        }

        if (ctx.compoundStmt() != null) {
            results?.put(ctx, results?.get(ctx.compoundStmt()))
        }
    }
}