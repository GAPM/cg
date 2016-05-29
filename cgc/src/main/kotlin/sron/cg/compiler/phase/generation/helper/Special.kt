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

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.INVOKESTATIC
import sron.cg.symbol.Function

private fun gAddNodes(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, "sron/cg/runtime/rt/RT", "gAddNodes",
            "(Lsron/cg/runtime/graph/Graph;I)Lsron/cg/runtime/graph/Graph;", false);
}

private fun dgAddNodes(mv: MethodVisitor) {
    mv.visitMethodInsn(INVOKESTATIC, "sron/cg/runtime/rt/RT", "dgAddNodes",
            "(Lsron/cg/runtime/graph/DiGraph;I)Lsron/cg/runtime/graph/DiGraph;", false);
}

fun handleSpecial(mv: MethodVisitor, function: Function) {
    when (function.name) {
        "g_add_nodes" -> gAddNodes(mv)
        "dg_add_nodes" -> dgAddNodes(mv)
    }
}
