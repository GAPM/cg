package grpc
package lib
package symbol

import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode

class Location(lineno: Int) {
  def this(token: Token) = this(token.getLine)

  def this(tn: TerminalNode) = this(tn.getSymbol)

  override def toString = s"$lineno".toString
}
