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
        var r = results?.get(ctx) ?: UnitResult()
        r.returns = v
        results?.put(ctx, r)
    }

    /**
     *
     */
    private fun getReturns(ctx: ParserRuleContext): Boolean =
            results?.get(ctx)?.returns ?: false

    /**
     * Reports that `continue` or `break` were used outside a loop.
     *
     * @param location The location of the error
     * @param word `continue` or `break`
     */
    private fun controlStmtError(location: Location, word: String) =
            addError(location, "`$word` not inside a loop")

    /**
     * Reports that a non-empty return is inside a `void` function.
     *
     * @param location The location of the error
     */
    private fun nonEmptyReturnError(location: Location) =
            addError(location, "non-empty return in void function `$fName`")

    /**
     * Reports that a empty return is inside a non-void function.
     *
     * @param location The location of the error
     */
    private fun emptyReturnError(location: Location) =
            addError(location, "empty return in non-void function `$fName`")

    /**
     * Reports that a non-void function doesn't guarantee a return.
     *
     * @param location The location of the function
     */
    private fun notAllPathsReturnError(location: Location) =
            addError(location, "in function $fName: not all paths have a return")

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
            controlStmtError(Location(ctx.start), "continue")
        }
    }

    /**
     * Checks whenever the phase enters a `break` statement that it is inside
     * a loop.
     */
    override fun enterBreak(ctx: BreakContext) {
        super.enterBreak(ctx)
        if (!insideLoop) {
            controlStmtError(Location(ctx.start), "break")
        }
    }

    /**
     * Marks that the phase entered a function definition, saving its name and
     * return type.
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
     * return type.
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
     * Checks whenever the phase leaves an if statement that all branches have a
     * return statement.
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

        ctx.elsec()?.let {
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
     * that all paths in a function have a return) and reports if a void
     * function has non-empty return or a non-void function has empty return.
     */
    override fun enterReturn(ctx: ReturnContext) {
        super.enterReturn(ctx)
        setReturns(ctx, true)
        val location = Location(ctx.start)

        if (currentFunctionType == Type.void) {
            if (ctx.expr() != null) {
                nonEmptyReturnError(location)
            }
        } else {
            if (ctx.expr() == null) {
                emptyReturnError(location)
            }
        }
    }

    /**
     * Sets the result of a simple statement to the same result of its used
     * child.
     */
    override fun exitSimpleStmt(ctx: SimpleStmtContext) {
        super.exitSimpleStmt(ctx)

        ctx.vdec()?.let {
            results?.put(ctx, results?.get(it))
        }

        ctx.assign()?.let {
            results?.put(ctx, results?.get(it))
        }

        ctx.controlStmt()?.let {
            results?.put(ctx, results?.get(it))
        }

        ctx.expr()?.let {
            results?.put(ctx, results?.get(it))
        }
    }

    /**
     * Sets the result of a compound statement to the same result of its used
     * child.
     */
    override fun exitCompoundStmt(ctx: CompoundStmtContext) {
        super.exitCompoundStmt(ctx)

        ctx.ifc()?.let {
            results?.put(ctx, results?.get(it))
        }

        ctx.forc()?.let {
            results?.put(ctx, results?.get(it))
        }

        ctx.whilec()?.let {
            results?.put(ctx, results?.get(it))
        }
    }

    /**
     * Sets the result of a statement to the same result of its used child.
     */
    override fun exitStmt(ctx: StmtContext) {
        super.exitStmt(ctx)

        ctx.simpleStmt()?.let {
            results?.put(ctx, results?.get(it))
        }

        ctx.compoundStmt()?.let {
            results?.put(ctx, results?.get(it))
        }
    }
}
