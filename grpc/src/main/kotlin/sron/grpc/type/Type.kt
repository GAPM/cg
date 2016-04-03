/*
 * Copyright 2016 Simón Oroño & La Universidad del Zulia
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

package sron.grpc.type

import org.antlr.v4.runtime.tree.TerminalNode
import sron.grpc.compiler.internal.GrpLexer
import sron.grpc.compiler.internal.GrpParser

enum class Type {
    byte,
    short,
    int,
    long,
    float,
    double,
    bool,
    void,
    string,
    char,

    ERROR,
    INTEGER_CONSTANT
}

fun GrpParser.TypeContext.toGrpType(): Type {
    val tn = this.getChild(0) as TerminalNode
    val t = tn.symbol.type

    return when (t) {
        GrpLexer.BYTE -> Type.byte
        GrpLexer.SHORT -> Type.short
        GrpLexer.INT -> Type.int
        GrpLexer.LONG -> Type.long
        GrpLexer.FLOAT -> Type.float
        GrpLexer.DOUBLE -> Type.double
        GrpLexer.BOOL -> Type.bool
        GrpLexer.VOID -> Type.void
        GrpLexer.STRING -> Type.string
        GrpLexer.CHAR -> Type.char
        else -> Type.ERROR
    }
}

fun Type.toJVMDescriptor() = when (this) {
    Type.byte -> "B"
    Type.short -> "S"
    Type.int -> "I"
    Type.long -> "J"
    Type.float -> "F"
    Type.double -> "D"
    Type.bool -> "Z"
    Type.void -> "V"
    Type.string -> "Ljava/lang/String"
    Type.char -> "C"

    else -> ""
}
