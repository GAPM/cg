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
    mv.visitMethodInsn(INVOKESTATIC, RT_IO_CLASS, "read",
            "()Ljava/lang/String;", false)
}

private fun perror(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, ERROR_CLASS, "perror", "()V", false)
}

private fun serror(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, ERROR_CLASS, "serror",
            "()Ljava/lang/String;", false)
}

private fun error(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, ERROR_CLASS, "error", "()Z", false)
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

private fun gContainsNode(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "gContainsNode",
            "(L$GRAPH_CLASS;I)Z", false)
}

private fun dgContainsNode(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "dgContainsNode",
            "(L$DIGRAPH_CLASS;I)Z", false)
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

private fun gContainsEdge(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "gContainsEdge",
            "(L$GRAPH_CLASS;II)Z", false)
}

private fun dgContainsEdge(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, RT_CLASS, "dgContainsEdge",
            "(L$DIGRAPH_CLASS;II)Z", false)
}

private fun gRemoveLoops(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, GRAPH_CLASS, "removeLoops",
            "()V", false)
}

private fun dgRemoveLoops(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, DIGRAPH_CLASS, "removeLoops",
            "()V", false)
}

private fun gShortestPath(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, GRAPH_CLASS, "shortestPath",
            "(I)L$GRAPH_CLASS;", false)
}

private fun dgShortestPath(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, DIGRAPH_CLASS, "shortestPath",
            "(I)L$DIGRAPH_CLASS;", false)
}

private fun gTransitivity(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, GRAPH_CLASS, "transitivityClosure",
            "()L$GRAPH_CLASS;", false)
}

private fun dgTransitivity(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKEVIRTUAL, DIGRAPH_CLASS, "transitivityClosure",
            "()L$DIGRAPH_CLASS;", false)
}

fun handleSpecial(mv: MethodVisitor, function: Function) {
    when (function.name) {
        "read" -> read(mv)

        "perror" -> perror(mv)
        "serror" -> serror(mv)
        "error" -> error(mv)

        "g_size" -> gSize(mv)
        "dg_size" -> dgSize(mv)

        "g_add_nodes" -> gAddNodes(mv)
        "dg_add_nodes" -> dgAddNodes(mv)

        "g_contains_node" -> gContainsNode(mv)
        "dg_contains_node" -> dgContainsNode(mv)

        "g_add_edge" -> gAddEdge(mv)
        "dg_add_edge" -> dgAddEdge(mv)

        "g_remove_edge" -> gRemoveEdge(mv)
        "dg_remove_edge" -> dgRemoveEdge(mv)

        "g_contains_edge" -> gContainsEdge(mv)
        "dg_contains_edge" -> dgContainsEdge(mv)

        "g_remove_loops" -> gRemoveLoops(mv)
        "dg_remove_loops" -> dgRemoveLoops(mv)

        "g_shortest_path" -> gShortestPath(mv)
        "dg_shortest_path" -> dgShortestPath(mv)

        "g_transitivity" -> gTransitivity(mv)
        "dg_transitivity" -> dgTransitivity(mv)
    }

}
