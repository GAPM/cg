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

package sron.cg.compiler.backend.jvm

import sron.cg.compiler.lang.*
import sron.cg.compiler.symbol.Signature

const val BITARRAY_CLASS_NAME = "sron/cg/lang/BitArray"
const val BITMATRIX_CLASS_NAME = "sron/cg/lang/BitMatrix"
const val GRAPH_CLASS_NAME = "sron/cg/lang/Graph"
const val DIGRAPH_CLASS_NAME = "sron/cg/lang/DiGraph"

fun AtomType.jvmDescriptor(): String = when (this) {
    AtomType.int -> "I"
    AtomType.float -> "F"
    AtomType.bool -> "Z"
    AtomType.char -> "C"
    AtomType.string -> "Ljava/lang/String;"
    AtomType.graph -> "L$GRAPH_CLASS_NAME;"
    AtomType.digraph -> "L$DIGRAPH_CLASS_NAME;"
    AtomType.void -> "V"
}

fun ArrayType.jvmDescriptor() = if (innerType == AtomType.bool) {
    "L$BITARRAY_CLASS_NAME;"
} else if (innerType == ArrayType(AtomType.bool)) {
    "L$BITMATRIX_CLASS_NAME;"
} else {
    "[${innerType.jvmDescriptor()}"
}

fun Type.jvmDescriptor(): String = when (this) {
    is AtomType -> this.jvmDescriptor()
    is ArrayType -> this.jvmDescriptor()

    else -> throw IllegalStateException()
}

fun Type.arrayDescriptor(): String = when (this) {
    AtomType.string -> "java/lang/String"
    AtomType.graph -> GRAPH_CLASS_NAME
    AtomType.digraph -> DIGRAPH_CLASS_NAME
    is ArrayType -> "[${this.innerType.jvmDescriptor()}"

    else -> throw IllegalStateException()
}

fun toJVMSignature(s: Signature, ret: Type): String {
    val params = s.map(Type::jvmDescriptor).joinToString(separator = "")
    return "($params)${ret.jvmDescriptor()}"
}
