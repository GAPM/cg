/*
 * Copyright 2016 Simón Oroño
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sron.grpc.compiler

import org.antlr.v4.runtime.tree.TerminalNode
import sron.grpc.compiler.internal.GrpErrorListener
import sron.grpc.compiler.internal.GrpLexer
import sron.grpc.compiler.internal.GrpParser
import sron.grpc.compiler.internal.GrpParser.TypeContext
import sron.grpc.symbol.Type

private var id = 0

fun GrpParser.withFileName(fileName: String): GrpParser {
    this.removeErrorListeners()
    this.addErrorListener(GrpErrorListener(fileName))
    return this
}

fun JVMArch(): Int {
    val arch = System.getProperty("os.arch")
    return if (arch.contains("64")) 64 else 32
}

fun TypeContext.toGrpType(): Type {
    val tn = this.getChild(0) as TerminalNode
    val t = tn.symbol.type

    return when (t) {
        GrpLexer.INT -> if (JVMArch() == 64) Type.int64 else Type.int32
        GrpLexer.INT8 -> Type.int8
        GrpLexer.INT16 -> Type.int16
        GrpLexer.INT32 -> Type.int32
        GrpLexer.INT64 -> Type.int64
        GrpLexer.FLOAT -> Type.float
        GrpLexer.DOUBLE -> Type.double
        GrpLexer.UINT -> if (JVMArch() == 64) Type.uint64 else Type.uint32
        GrpLexer.UINT8 -> Type.uint8
        GrpLexer.UINT16 -> Type.uint16
        GrpLexer.UINT32 -> Type.uint32
        GrpLexer.UINT64 -> Type.uint64
        GrpLexer.BOOL -> Type.bool
        GrpLexer.VOID -> Type.void
        GrpLexer.STRING -> Type.string
        GrpLexer.CHAR -> Type.char
        else -> Type.error
    }
}

fun Type.isNumericType(): Boolean = when (this) {
    Type.char, Type.string, Type.void, Type.bool -> false
    else -> true
}

fun String.removeQuotes(): String {
    if (this.matches(Regex("\\\".*?\\\""))) {
        return this.substring(1, this.length - 1)
    } else {
        return ""
    }
}

fun nextId(): Int = id++
