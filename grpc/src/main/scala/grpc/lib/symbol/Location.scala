package grpc.lib.symbol

import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode

class Location(lineno: Int, columnno: Int) {
  def this(token: Token) = this(token.getLine, token.getCharPositionInLine)

  def this(tn: TerminalNode) = this(tn.getSymbol)

  override def toString = s"$lineno:$columnno".toString
}
