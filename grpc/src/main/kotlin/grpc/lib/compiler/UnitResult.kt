package grpc.lib.compiler

import grpc.lib.symbol.Type

class UnitResult {
    public var type: Type = Type.error
    public var returns: Boolean = false
    public var assignable: Boolean = false
}
