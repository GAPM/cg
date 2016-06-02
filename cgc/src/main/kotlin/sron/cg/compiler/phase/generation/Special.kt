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
import org.objectweb.asm.Opcodes.INVOKESTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import sron.cg.compiler.symbol.Function

private fun read(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, "sron/cg/lang/rt/IO", "read",
            "()Ljava/lang/String;", false)
}

private fun gAddNodes(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, "sron/cg/lang/rt/RT", "gAddNodes",
            "(Lsron/cg/lang/Graph;I)Lsron/cg/lang/Graph;", false);
}

private fun dgAddNodes(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, "sron/cg/lang/rt/RT", "dgAddNodes",
            "(Lsron/cg/lang/DiGraph;I)Lsron/cg/lang/DiGraph;", false);
}

private fun gRemoveLoops(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, "sron/cg/lang/Graph", "removeLoops",
            "()V", false)
}

private fun dgRemoveLoops(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, "sron/cg/lang/DiGraph", "removeLoops",
            "()V", false)
}

fun handleSpecial(mv: MethodVisitor, function: Function) {
    when (function.name) {
        "read" -> read(mv)

        "g_add_nodes" -> gAddNodes(mv)
        "dg_add_nodes" -> dgAddNodes(mv)

        "g_remove_loops" -> gRemoveLoops(mv)
        "dg_remove_loops" -> dgRemoveLoops(mv)
    }
}