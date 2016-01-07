package grpc.lib.compiler

import grpc.lib.compiler.internal.GrpLexer
import grpc.lib.compiler.internal.GrpParser.TypContext
import grpc.lib.symbol.Type
import org.antlr.v4.runtime.tree.TerminalNode

fun getJVMArch(): Int {
    val arch = System.getProperty("os.arch")
    return if (arch.contains("64")) 64 else 32
}

fun tokIdxToType(ctx: TypContext?): Type {
    val tn = ctx?.getChild(0) as TerminalNode
    val t = tn.symbol.type

    return when (t) {
        GrpLexer.INT -> if (getJVMArch() == 64) Type.int64 else Type.int32
        GrpLexer.INT8 -> Type.int8
        GrpLexer.INT16 -> Type.int16
        GrpLexer.INT32 -> Type.int32
        GrpLexer.INT64 -> Type.int64
        GrpLexer.FLOAT -> Type.float
        GrpLexer.DOUBLE -> Type.double
        GrpLexer.UINT -> if (getJVMArch() == 64) Type.uint64 else Type.uint32
        GrpLexer.UINT8 -> Type.uint8
        GrpLexer.UINT16 -> Type.uint16
        GrpLexer.UINT32 -> Type.uint32
        GrpLexer.UINT64 -> Type.uint64
        GrpLexer.BOOL -> Type.bool
        GrpLexer.VOID -> Type.void
        GrpLexer.STRING -> Type.string
        GrpLexer.CHAR -> Type.char
        else -> Type.none
    }
}

fun isNumeric(type: Type): Boolean = when (type) {
    Type.char, Type.string, Type.void, Type.bool -> false
    else -> true
}