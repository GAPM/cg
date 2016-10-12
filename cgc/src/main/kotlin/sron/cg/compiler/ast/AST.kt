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

package sron.cg.compiler.ast

import sron.cg.compiler.lang.Type
import sron.cg.compiler.symbol.Scope
import sron.cg.compiler.symbol.Signature

data class Point(val line: Int, val column: Int) {
    override fun toString() = "$line:$column"
}

data class Location(val start: Point, val end: Point) {
    override fun toString() = "$start - $end"
}

abstract class Node(val location: Location) {
    var parent: Node = VoidNode
    abstract val scope: Scope
}

object VoidNode : Node(Location(Point(0, 0), Point(0, 0))) {
    override val scope = Scope(null, null)
}

class Parameter(val id: String, val type: Type, location: Location) :
        Node(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }
}

class FuncDef(val id: String, val type: Type, val params: List<Parameter>,
              val body: List<Stmt>, location: Location) : Node(location) {

    override val scope by lazy {
        Scope(parent.scope, this.toString())
    }

    val signature = Signature(params.map { it.type }, type)

    init {
        params.forEach { it.parent = this }
        body.forEach { it.parent = this }
    }
}

class Init(val funcDef: List<FuncDef>, val varDec: List<VarDec>,
           location: Location) : Node(location) {

    override val scope by lazy {
        Scope(null, this.toString())
    }

    init {
        funcDef.forEach { it.parent = this }
        varDec.forEach { it.parent = this }
    }
}
