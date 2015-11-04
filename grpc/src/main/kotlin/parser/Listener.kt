package parser

import parser.internal.GrpBaseListener
import parser.internal.GrpParser.LiteralContext

class Listener : GrpBaseListener() {
    override fun exitLiteral(ctx: LiteralContext?) {
        super.exitLiteral(ctx)

        println("CharLit: " + ctx?.CharLit())
        println("IntLit: " + ctx?.IntLit())
        println("FloatLit: " + ctx?.FloatLit())
        println("StringLit: " + ctx?.StringLit())
    }
}