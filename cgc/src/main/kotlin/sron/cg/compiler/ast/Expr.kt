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

import sron.cg.compiler.lang.*
import sron.cg.compiler.symbol.Scope

enum class GraphType {
    GRAPH,
    DIGRAPH
}

abstract class Expr(location: Location) : Stmt(location) {
    var type: Type = ERROR
}

abstract class Atom(location: Location) : Expr(location)

class Literal(val text: String, location: Location) :
        Atom(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }
}

class VarName(val id: String, location: Location) : Atom(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }
}

/**
 * Class used to store edges as seen in the source code. Edge is not a
 * CG type, but might be in a future.
 */
class Edge(val source: Expr, val target: Expr)

class GraphLit(val graphType: GraphType, val size: Expr, val edges: List<Edge>,
               location: Location) : Atom(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        size.parent = this
        for (edge in edges) {
            edge.source.parent = this
            edge.target.parent = this
        }
    }
}

class FunctionCall(val id: String, val args: List<Expr>, location: Location) :
        Atom(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        for (arg in args) {
            arg.parent = this
        }
    }
}

class Cast(val expr: Expr, location: Location) :
        Atom(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        expr.parent = this
    }
}

class ArrayLit(val elems: List<Expr>, location: Location) : Atom(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        for (elem in elems) {
            elem.parent = this
        }
    }
}

class ArrayAccess(val array: Expr, val subscript: Expr, location: Location) :
        Atom(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        array.parent = this
        subscript.parent = this
    }
}

class UnaryExpr(val op: Operator, val expr: Expr, location: Location) :
        Expr(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        expr.parent = this
    }
}

class BinaryExpr(val op: Operator, val lhs: Expr, val rhs: Expr,
                 location: Location) : Expr(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        lhs.parent = this
        rhs.parent = this
    }
}
