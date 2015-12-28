package grpc.lib.compiler

import grpc.lib.symbol.Type

class UnitResult {
  private var typ: Type.Value = Type.none
  private var returns = false

  def setType(typ: Type.Value) {
    this.typ = typ
  }

  def setReturns(returns: Boolean): Unit = {
    this.returns = returns
  }

  def getType: Type.Value = typ

  def getReturns: Boolean = returns
}
