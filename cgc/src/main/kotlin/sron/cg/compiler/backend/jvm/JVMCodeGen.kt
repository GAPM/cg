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
import org.objectweb.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.pass.Pass
import java.io.File

class JVMCodeGen(state: State) : Pass(state) {
    private val cw = ClassWriter(COMPUTE_FRAMES)
    private val className = state.parameters.output
    private lateinit var mv: MethodVisitor

    private fun VarDec.globalGen() {
        val fv = cw.visitField(ACC_PUBLIC + ACC_STATIC, id, type.jvmDescriptor(), null, null)
        fv.visitEnd()
    }

    private fun FuncDef.gen() {
        val desc = toJVMSignature(signature, type)
        mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, id, desc, null, null)
        mv.visitCode()

        //todo: statements

        mv.visitMaxs(0, 0)
        mv.visitEnd()
    }

    override fun exec(ast: Init) {
        cw.visit(V1_8, ACC_SUPER + ACC_PUBLIC, className, null,
                "java/lang/Object", null)

        constructor(cw, className)

        for (vd in ast.varDec) {
            vd.globalGen()
        }

        for (fd in ast.funcDef) {
            fd.gen()
        }

        cw.visitEnd()
        val bytes = cw.toByteArray()

        File("$className.class").outputStream().use {
            it.write(bytes)
        }
    }
}
