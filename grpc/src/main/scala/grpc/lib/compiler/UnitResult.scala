package grpc
package lib
package compiler

import grpc.lib.symbol.Type
import grpc.lib.symbol.Type.Type

class UnitResult {
  private var typ: Type = Type.none
  private var returns = false

  def setType(typ: Type) {
    this.typ = typ
  }

  def setReturns(returns: Boolean): Unit = {
    this.returns = returns
  }

  def getType: Type = typ

  def getReturns: Boolean = returns
}
