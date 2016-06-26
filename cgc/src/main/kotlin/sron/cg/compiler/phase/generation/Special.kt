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

private fun assert(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_ASSERT_CLASS, "assertF", "(Z)V", false)
}

private fun read(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_IO_CLASS, "read",
            "()Ljava/lang/String;", false)
}

private fun gSize(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, GRAPH_CLASS, "getSize", "()I", false)
}

private fun dgSize(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, DIGRAPH_CLASS, "getSize", "()I", false)
}

private fun gAddNodes(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "gAddNodes",
            "(L$GRAPH_CLASS;I)L$GRAPH_CLASS;", false)
}

private fun dgAddNodes(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "dgAddNodes",
            "(L$DIGRAPH_CLASS;I)L$DIGRAPH_CLASS;", false)
}

private fun gAddEdge(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "gAddEdge",
            "(L$GRAPH_CLASS;II)V", false)
}

private fun dgAddEdge(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "dgAddEdge",
            "(L$DIGRAPH_CLASS;II)V", false)
}

private fun gRemoveEdge(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "gRemoveEdge",
            "(L$GRAPH_CLASS;II)V", false)
}

private fun dgRemoveEdge(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "dgRemoveEdge",
            "(L$DIGRAPH_CLASS;II)V", false)
}

private fun gRemoveLoops(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, GRAPH_CLASS, "removeLoops",
            "()V", false)
}

private fun dgRemoveLoops(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, DIGRAPH_CLASS, "removeLoops",
            "()V", false)
}

fun handleSpecial(mv: MethodVisitor, function: Function) {
    when (function.name) {
        "assert" -> assert(mv)
        "read" -> read(mv)

        "g_size" -> gSize(mv)
        "dg_size" -> dgSize(mv)

        "g_add_nodes" -> gAddNodes(mv)
        "dg_add_nodes" -> dgAddNodes(mv)

        "g_add_edge" -> gAddEdge(mv)
        "dg_add_edge" -> dgAddEdge(mv)

        "g_remove_edge" -> gRemoveEdge(mv)
        "dg_remove_edge" -> dgRemoveEdge(mv)

        "g_remove_loops" -> gRemoveLoops(mv)
        "dg_remove_loops" -> dgRemoveLoops(mv)
    }

}
