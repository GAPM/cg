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

package sron.cg.compiler.phase.generation.helper

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cg.type.Type

fun not(mv: MethodVisitor) {
    val start = Label()
    val isTrue = Label()
    val end = Label()

    mv.visitLabel(start)
    mv.visitJumpInsn(IFNE, isTrue)

    mv.visitInsn(ICONST_1)
    mv.visitJumpInsn(GOTO, end)

    mv.visitLabel(isTrue)
    mv.visitInsn(ICONST_0)

    mv.visitLabel(end)
}

fun minus(mv: MethodVisitor, type: Type) {
    if (type == Type.int) {
        mv.visitInsn(INEG)
    } else if (type == Type.float) {
        mv.visitInsn(FNEG)
    }
}