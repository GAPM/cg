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

package sron.cg.type

import org.antlr.v4.runtime.tree.TerminalNode
import sron.cg.compiler.internal.CGLexer
import sron.cg.compiler.internal.CGParser

enum class Type {
    int,
    float,
    bool,
    void,
    string,
    graph,
    digraph,

    ERROR
}

fun CGParser.TypeContext.toCGType(): Type {
    val tn = this.getChild(0) as TerminalNode
    val t = tn.symbol.type

    return when (t) {
        CGLexer.INT -> Type.int
        CGLexer.FLOAT -> Type.float
        CGLexer.BOOL -> Type.bool
        CGLexer.VOID -> Type.void
        CGLexer.STRING -> Type.string
        CGLexer.GRAPH -> Type.graph
        CGLexer.DIGRAPH -> Type.digraph
        else -> Type.ERROR
    }
}

fun Type.descriptor() = when (this) {
    Type.int -> "I"
    Type.float -> "F"
    Type.bool -> "Z"
    Type.void -> "V"
    Type.string -> "Ljava/lang/String;"
    Type.graph -> "Lsron/cg/runtime/graph/Graph;"
    Type.digraph -> "Lsron/cg/runtime/graph/DiGraph;"

    else -> ""
}

fun Type.fullName() = when (this) {
    Type.graph -> "sron/cg/runtime/graph/Graph"
    Type.digraph -> "sron/cg/runtime/graph/DiGraph"
    else -> ""
}

fun Type.defaultValue(): Any? = when (this) {
    Type.int -> 0
    Type.float -> 0.0f
    Type.bool -> false
    Type.string -> ""
    else -> null
}
