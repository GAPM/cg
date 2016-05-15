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
import sron.cg.compiler.State
import sron.cg.symbol.Function
import sron.cg.type.JVMDescriptor

fun signatureString(fd: Function): String {
    var result = "("
    for (arg in fd.args) {
        result += arg.type.JVMDescriptor()
    }
    result += ")"
    result += fd.type.JVMDescriptor()

    return result
}

fun getVarIndex(s: State, fName: String, vName: String): Int {
    val idx = s.varIndex[fName]!![vName]!!
    return idx
}

fun handleSpecial(mv: MethodVisitor, fd: Function) {

}