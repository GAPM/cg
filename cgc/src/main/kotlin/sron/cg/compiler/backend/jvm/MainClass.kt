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

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*

fun constructor(cw: ClassWriter, fileName: String) {
    val mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
    mv.visitCode()

    val start = Label()
    mv.visitLabel(start)

    mv.visitVarInsn(ALOAD, 0)
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
    mv.visitInsn(RETURN)

    val end = Label()
    mv.visitLabel(end)

    mv.visitLocalVariable("this", "L$fileName;", null, start, end, 0)

    mv.visitMaxs(0, 0)
    mv.visitEnd()
}
