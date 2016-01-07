package grpc.lib.symbol

import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode

class Location(val line: Int, val column: Int) {
    constructor(token: Token?) : this(token!!.line, token.charPositionInLine)

    constructor(tn: TerminalNode?) : this(tn!!.symbol)

    override fun toString(): String = "$line:$column"
}