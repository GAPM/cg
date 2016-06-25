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

package sron.cg.compiler.phase.generation

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.State
import sron.cg.compiler.type.Type

fun getVarIndex(s: State, fName: String, vName: String): Int {
    val idx = s.varIndex[fName]!![vName]!!
    return idx
}

fun withoutQuotes(s: String) = try {
    s.substring(1, s.length - 1)
} catch (e: IndexOutOfBoundsException) {
    ""
}

fun pushDefaultToStack(mv: MethodVisitor, type: Type) {
    when (type) {
        Type.int -> mv.visitInsn(ICONST_0)
        Type.float -> mv.visitInsn(FCONST_0)
        Type.bool -> mv.visitInsn(ICONST_0)
        Type.string -> mv.visitLdcInsn("")
        Type.graph, Type.digraph -> {
            mv.visitTypeInsn(NEW, type.fullName())
            mv.visitInsn(DUP)
            mv.visitInsn(ICONST_0)
            mv.visitMethodInsn(INVOKESPECIAL, type.fullName(), "<init>", "(I)V", false)
        }
        else -> {
        }
    }
}
