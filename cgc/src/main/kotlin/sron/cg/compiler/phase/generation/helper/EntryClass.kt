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

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.ast.GlVarDec
import sron.cg.type.Type
import sron.cg.type.descriptor

fun constructor(cw: ClassWriter) {
    val mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
    mv.visitCode()
    val ls = Label()
    mv.visitLabel(ls)
    mv.visitVarInsn(ALOAD, 0)
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
    mv.visitInsn(RETURN)
    val le = Label()
    mv.visitLabel(le)
    mv.visitLocalVariable("this", "LEntryPoint;", null, ls, le, 0)
    mv.visitMaxs(0, 0)
    mv.visitEnd()
}

fun initializer(cw: ClassWriter, glVarDec: List<GlVarDec>) {
    val mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
    mv.visitCode()

    val ls = Label()
    mv.visitLabel(ls)

    for (gvd in glVarDec) {
        if (gvd.type == Type.graph || gvd.type == Type.digraph) {
            pushDefaultToStack(mv, gvd.type)
            mv.visitFieldInsn(PUTSTATIC, "EntryPoint", gvd.name, gvd.type.descriptor())
        }
    }

    val le = Label()
    mv.visitLabel(le)

    mv.visitInsn(RETURN)
    mv.visitMaxs(0, 0)
    mv.visitEnd()
}

fun main(cw: ClassWriter) {
    val mv = cw.visitMethod(ACC_STATIC + ACC_PUBLIC, "main", "([Ljava/lang/String;)V", null, null)
    mv.visitCode()

    val ls = Label()
    mv.visitLabel(ls)

    mv.visitMethodInsn(INVOKESTATIC, "EntryPoint", "main", "()V", false)

    val le = Label()
    mv.visitLabel(le)

    mv.visitLocalVariable("args", "[Ljava/lang/String;", null, ls, le, 0)

    mv.visitInsn(RETURN)
    mv.visitMaxs(0, 0)
    mv.visitEnd()
}
