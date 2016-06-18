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

package sron.cg.compiler.phase.globals

import sron.cg.compiler.symbol.Function
import sron.cg.compiler.symbol.Location
import sron.cg.compiler.symbol.Variable
import sron.cg.compiler.type.Type

private fun function(name: String, type: Type, vararg pairs: Pair<String, Type>): Function {
    val scope = "global.$name"

    val args = Array(pairs.size) { i ->
        Variable(pairs[i].first, pairs[i].second, scope, Location(-1))
    }

    val f = Function(name, "global", type, Location(-1), *args)
    f.isSpecial = true

    return f
}

val runtimeFunctions by lazy {
    val result = mutableListOf<Function>()

    result += function("read", Type.string)

    result += function("g_size", Type.int,
            "g" to Type.graph)
    result += function("dg_size", Type.int,
            "g" to Type.digraph)

    result += function("g_add_nodes", Type.graph,
            "g" to Type.graph,
            "n" to Type.int)
    result += function("dg_add_nodes", Type.digraph,
            "g" to Type.digraph,
            "n" to Type.int)

    result += function("g_add_edge", Type.void,
            "g" to Type.graph,
            "a" to Type.int,
            "b" to Type.int)
    result += function("dg_add_edge", Type.void,
            "g" to Type.digraph,
            "a" to Type.int,
            "b" to Type.int)

    result += function("g_remove_edge", Type.void,
            "g" to Type.graph,
            "a" to Type.int,
            "b" to Type.int)
    result += function("dg_remove_edge", Type.void,
            "g" to Type.digraph,
            "a" to Type.int,
            "b" to Type.int)

    result += function("g_remove_loops", Type.void,
            "g" to Type.graph)
    result += function("dg_remove_loops", Type.void,
            "g" to Type.digraph)

    result
}
