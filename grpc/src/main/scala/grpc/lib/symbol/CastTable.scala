package grpc.lib.symbol

import grpc.lib.symbol.Type.Type

object CastTable {
  private val numeric = List(
    Type.int8, Type.int16, Type.int32, Type.int64,
    Type.uint8, Type.uint16, Type.uint32, Type.uint64,
    Type.float, Type.double
  )

  private val tab = Map(
    Type.int8 -> numeric,
    Type.int16 -> numeric,
    Type.int32 -> numeric,
    Type.int64 -> numeric,
    Type.uint8 -> numeric,
    Type.uint16 -> numeric,
    Type.uint32 -> numeric,
    Type.uint64 -> numeric,
    Type.float -> numeric,
    Type.double -> numeric,
    Type.bool -> numeric,
    Type.char -> List(Type.string),
    Type.string -> List()
  )

  def check(typ1: Type, typ2: Type): Boolean = {
    if (tab.contains(typ1)) {
      tab(typ1).contains(typ2)
    } else {
      false
    }
  }
}
