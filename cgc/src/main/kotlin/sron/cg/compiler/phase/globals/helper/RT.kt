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

package sron.cg.compiler.phase.globals.helper

import sron.cg.compiler.symbol.Function
import sron.cg.compiler.symbol.Location
import sron.cg.compiler.symbol.Variable
import sron.cg.compiler.type.Type

val rtFunctions by lazy {
    val g = "global"
    val result = mutableListOf<Function>()

    result += Function("g_add_nodes", g, Type.graph, Location(-1),
            Variable("g", Type.graph, "$g.g_add_nodes", Location(-1)),
            Variable("n", Type.int, "$g.g_add_nodes", Location(-1))
    )
    result += Function("dg_add_nodes", g, Type.digraph, Location(-1),
            Variable("g", Type.digraph, "$g.dg_add_nodes", Location(-1)),
            Variable("n", Type.int, "$g.dg_add_nodes", Location(-1))
    )

    for (f in result) {
        f.isSpecial = true
    }

    result
}
