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

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.type.Type

/**
 * Generates code for boolean not and graph negation
 *
 * !i gets translated to (assuming that i is on top of the stack and it's a
 * boolean):
 *
 * IFNE isTrue
 * ICONST_1
 * GOTO end
 * isTrue:
 * ICONST_0
 * end:
 *
 * If i has a graph type, code for the negation() method is generated
 *
 * @param mv The method visitor
 * @param type The type of the operand
 */
fun not(mv: MethodVisitor, type: Type) {
    if (type == Type.bool) {
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
    } else if (type == Type.graph) {
        mv.visitMethodInsn(INVOKEVIRTUAL, "sron/cg/lang/Graph", "negation",
                "()Lsron/cg/lang/Graph;", false);
    } else {
        mv.visitMethodInsn(INVOKEVIRTUAL, "sron/cg/lang/DiGraph", "negation",
                "()Lsron/cg/lang/DiGraph;", false);
    }
}

/**
 * Generates code for unary minus operation
 *
 * @param mv The method visitor
 * @param type The type of the operand
 */
fun minus(mv: MethodVisitor, type: Type) {
    if (type == Type.int) {
        mv.visitInsn(INEG)
    } else if (type == Type.float) {
        mv.visitInsn(FNEG)
    }
}
