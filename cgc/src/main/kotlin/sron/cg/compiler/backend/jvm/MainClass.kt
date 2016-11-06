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

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.ast.VarDec
import sron.cg.compiler.lang.*
import sron.cg.compiler.lang.Type

fun init(cw: ClassWriter, fileName: String) {
    val mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
    mv.visitCode()

    val start = Label()
    mv.visitLabel(start)

    mv.visitVarInsn(ALOAD, 0)
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
    mv.visitInsn(RETURN)

    val end = Label()
    mv.visitLabel(end)
    mv.visitInsn(RETURN)

    mv.visitLocalVariable("this", "L$fileName;", null, start, end, 0)

    mv.visitMaxs(0, 0)
    mv.visitEnd()
}

private fun pushDefaultGraph(mv: MethodVisitor, type: Type) {
    val cls = when (type) {
        AtomType.graph -> GRAPH_CLASS_NAME
        AtomType.digraph -> DIGRAPH_CLASS_NAME
        else -> throw IllegalStateException()
    }

    mv.visitTypeInsn(NEW, cls)
    mv.visitInsn(DUP)
    mv.visitLdcInsn(0)
    mv.visitFieldInsn(INVOKESPECIAL, cls, "<init>", "(I)V")
}

private fun pushDefaultArray(mv: MethodVisitor, type: ArrayType) {
    mv.visitLdcInsn(0)

    when (type.innerType) {
        AtomType.int -> mv.visitIntInsn(NEWARRAY, T_INT)
        AtomType.float -> mv.visitIntInsn(NEWARRAY, T_FLOAT)
        AtomType.char -> mv.visitIntInsn(NEWARRAY, T_CHAR)
        AtomType.bool -> mv.visitIntInsn(NEWARRAY, T_BOOLEAN)
        AtomType.string, AtomType.graph, AtomType.digraph ->
            mv.visitTypeInsn(ANEWARRAY, type.innerType.arrayDescriptor())
        is ArrayType ->
            mv.visitTypeInsn(ANEWARRAY, type.innerType.arrayDescriptor())
    }
}

private fun pushDefault(mv: MethodVisitor, type: Type) {
    when (type) {
        AtomType.int -> mv.visitLdcInsn(0)
        AtomType.float -> mv.visitLdcInsn(0.0f)
        AtomType.char -> mv.visitLdcInsn(0.toChar())
        AtomType.bool -> mv.visitLdcInsn(false)
        AtomType.string -> mv.visitLdcInsn("")
        AtomType.graph, AtomType.digraph -> pushDefaultGraph(mv, type)
        is ArrayType -> pushDefaultArray(mv, type)

        else -> throw IllegalStateException()
    }
}

fun clinit(cw: ClassWriter, varDec: List<VarDec>, fileName: String) {
    val mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null)
    mv.visitCode()

    val start = Label()
    mv.visitLabel(start)

    for (vd in varDec) {
        val l = Label()
        mv.visitLabel(l)
        mv.visitLineNumber(vd.location.start.line, l)

        pushDefault(mv, vd.type)
        mv.visitFieldInsn(PUTSTATIC, fileName, vd.id, vd.type.jvmDescriptor())
    }

    val end = Label()
    mv.visitLabel(end)
    mv.visitInsn(RETURN)

    mv.visitMaxs(0, 0)
    mv.visitEnd()
}
