package grpc.lib.compiler.phase

import grpc.lib.compiler.internal.GrpParser
import grpc.lib.compiler.internal.GrpParser.*
import grpc.lib.compiler.tokIdxToType
import grpc.lib.symbol.Function
import grpc.lib.symbol.Location
import grpc.lib.symbol.SymType
import grpc.lib.symbol.Variable
import org.antlr.v4.runtime.ParserRuleContext
import java.util.*

/**
 * [Globals] is the compiler phase where all global symbols (functions and
 * variables) are stored in the symbol table.
 */
class Globals : Phase() {
    private var insideSimpleStmt = false

    /**
     * Reports a global function or variable redeclaration
     *
     * @param last The location of the redeclaration
     * @param first The location of the previously declared symbol
     * @param name The name of the symbol
     * @param symType  Whether it is a function or a variable
     */
    fun redeclarationError(last: Location, first: Location, name: String,
                           symType: SymType) {
        val t = when (symType) {
            SymType.VAR -> "global variable"
            SymType.FUNC -> "function"
        }

        val m = "redeclaration of $t `$name`. Previously declared at $first"
        addError(last, m)
    }

    /**
     * Marks whenever the phase enters in a simple statement. Variables being
     * declared inside a simple statement are not global.
     */
    override fun enterSimpleStmt(ctx: SimpleStmtContext?) {
        super.enterSimpleStmt(ctx)
        insideSimpleStmt = true
    }

    /**
     * Marks whenever the phase leaves a simple statement. Variables being
     * declared inside a simple statement are not global.
     */
    override fun exitSimpleStmt(ctx: SimpleStmtContext?) {
        super.exitSimpleStmt(ctx)
        insideSimpleStmt = false
    }

    /**
     * Inserts a function and its arguments into the symbol table.
     */
    override fun exitFdef(ctx: GrpParser.FdefContext) {
        super.exitFdef(ctx)

        val name = ctx.Identifier().text
        val type = tokIdxToType(ctx.typ())
        val location = Location(ctx.Identifier())
        val args = ArrayList<Variable>()

        val ar = ctx.argList().arg()
        for (a in ar) {
            val argName = a.Identifier().text
            val argType = tokIdxToType(a.typ())
            val argLoc = Location(a.Identifier())

            val v = Variable(argName, argType, name, argLoc)
            args.add(v)
        }

        val function = Function(name, type, location, args)

        val qry = symTab!!.getSymbol(name, SymType.FUNC)
        when (qry) {
            null -> {
                symTab!!.addSymbol(function)
                for (v in args) {
                    symTab!!.addSymbol(v)
                }
            }
            else -> {
                redeclarationError(location, qry.location, name, SymType.FUNC)
            }
        }
    }

    /**
     * Inserts a global variable into the symbol table.
     */
    override fun exitVdec(ctx: VdecContext) {
        super.exitVdec(ctx)

        if (!insideSimpleStmt) {
            val name = ctx.Identifier().text
            val type = tokIdxToType(ctx.typ())
            val location = Location(ctx.Identifier())

            val variable = Variable(name, type, "global", location)

            val qry = symTab!!.getSymbol(name, "global", SymType.VAR)
            when (qry) {
                null -> symTab!!.addSymbol(variable)
                else ->
                    redeclarationError(location, qry.location, name, SymType.VAR)
            }
        }
    }
}