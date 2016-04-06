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

package sron.cgpl.compiler.phase

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cgpl.compiler.internal.GrpParser.GlVarDecContext
import sron.cgpl.compiler.internal.GrpParser.InitContext
import sron.cgpl.type.toGrpType
import sron.cgpl.type.toJVMDescriptor
import java.io.File

class Generation : Phase() {
    private val cw = ClassWriter(0)
    lateinit private var mv: MethodVisitor
    lateinit private var fv: FieldVisitor

    override fun enterInit(ctx: InitContext) {
        super.enterInit(ctx)
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, "EntryPoint", null, "java/lang/Object", null)

        // Creation of default constructor
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
        mv.visitCode()
        val l0 = Label()
        mv.visitLabel(l0)
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        mv.visitInsn(RETURN)
        val l1 = Label()
        mv.visitLabel(l1)
        mv.visitLocalVariable("this", "LSimon;", null, l0, l1, 0);
        mv.visitMaxs(1, 1)
        mv.visitEnd()
    }

    override fun exitGlVarDec(ctx: GlVarDecContext) {
        super.exitGlVarDec(ctx)
        val name = ctx.Identifier().text
        val type = ctx.type().toGrpType()

        // TODO: initial value

        fv = cw.visitField(ACC_STATIC, name, type.toJVMDescriptor(), null, null)
        fv.visitEnd()
    }

    override fun exitInit(ctx: InitContext) {
        super.exitInit(ctx)
        cw.visitEnd()
        if (!parameters.justCheck) {
            val binaryForm = cw.toByteArray()

            File("EntryPoint.class").let {
                it.outputStream().use {
                    it.write(binaryForm)
                }
            }
        }
    }
}
