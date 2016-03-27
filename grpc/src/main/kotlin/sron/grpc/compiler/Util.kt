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

package sron.grpc.compiler

import org.antlr.v4.runtime.tree.TerminalNode
import sron.grpc.compiler.internal.GrpErrorListener
import sron.grpc.compiler.internal.GrpLexer
import sron.grpc.compiler.internal.GrpParser
import sron.grpc.compiler.internal.GrpParser.TypeContext
import sron.grpc.type.Type

private var id = 0

fun GrpParser.withFileName(fileName: String): GrpParser {
    this.removeErrorListeners()
    this.addErrorListener(GrpErrorListener(fileName))
    return this
}

fun TypeContext.toGrpType(): Type {
    val tn = this.getChild(0) as TerminalNode
    val t = tn.symbol.type

    return when (t) {
        GrpLexer.BYTE -> Type.BYTE
        GrpLexer.SHORT -> Type.SHORT
        GrpLexer.INT -> Type.INT
        GrpLexer.LONG -> Type.LONG
        GrpLexer.FLOAT -> Type.FLOAT
        GrpLexer.DOUBLE -> Type.DOUBLE
        GrpLexer.BOOL -> Type.BOOL
        GrpLexer.VOID -> Type.VOID
        GrpLexer.STRING -> Type.STRING
        GrpLexer.CHAR -> Type.CHAR
        else -> Type.ERROR
    }
}

fun nextId(): Int = id++
