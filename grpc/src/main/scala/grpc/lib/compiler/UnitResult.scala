package grpc.lib.compiler

import grpc.lib.symbol.Type

class UnitResult {
  private var typ: Type.Value = Type.error

  def setType(typ: Type.Value) = {
    this.typ = typ
  }

  def getType: Type.Value = typ
}
