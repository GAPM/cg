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
import org.objectweb.asm.Opcodes.*
import sron.cg.compiler.State
import sron.cg.compiler.ast.FuncDef
import sron.cg.compiler.ast.Identifier
import sron.cg.type.Type

fun identifier(mv: MethodVisitor, id: Identifier, s: State, fd: FuncDef) {
    val idx = getVarIndex(s, fd.name, id.name)

    when (id.type) {
        Type.int -> mv.visitVarInsn(ILOAD, idx)
        Type.float -> mv.visitVarInsn(FLOAD, idx)
        Type.bool -> mv.visitVarInsn(ILOAD, idx)
        Type.string -> mv.visitVarInsn(ALOAD, idx)
        else -> {
        }
    }
}
