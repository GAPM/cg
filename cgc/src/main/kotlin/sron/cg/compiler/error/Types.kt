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

package sron.cg.compiler.error

import sron.cg.compiler.ast.*

class VariableNotFoundInScope(vn: VarName) : Error {
    override val msg =
            """
            |${vn.location}:
            |  variable ${vn.id} was not found in current scope
            """.trimMargin()
}

class NonIntegerSize(gl: GraphLit) : Error {
    override val msg =
            """
            |${gl.location}:
            |  size not an integer in graph literal
            """.trimMargin()
}

class NonIntegerNode(expr: Expr) : Error {
    override val msg =
            """
            |${expr.location}:
            |  node index not an integer
            """.trimMargin()
}
