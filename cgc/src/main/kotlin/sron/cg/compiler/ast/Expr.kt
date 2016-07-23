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

import sron.cg.compiler.symbol.Location
import sron.cg.compiler.symbol.Variable
import sron.cg.compiler.type.Type

enum class GraphType {
    GRAPH,
    DIGRAPH
}

enum class Operator {
    ADD, SUB, MUL, DIV, MOD,

    AND, OR,

    NOT, MINUS, PLUS,

    LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,

    EQUAL,
    NOT_EQUAL;

    fun sign(): String = when (this) {
        Operator.ADD -> "+"
        Operator.SUB -> "-"
        Operator.MUL -> "*"
        Operator.DIV -> "/"
        Operator.MOD -> "%"
        Operator.AND -> "&&"
        Operator.OR -> "||"
        Operator.NOT -> "!"
        Operator.MINUS -> "-"
        Operator.PLUS -> "+"
        Operator.LESS -> "<"
        Operator.LESS_EQUAL -> "<="
        Operator.GREATER -> ">"
        Operator.GREATER_EQUAL -> ">="
        Operator.EQUAL -> "=="
        Operator.NOT_EQUAL -> "!="
    }
}

abstract class Expr(location: Location) : Stmt(location) {
    open var type = Type.ERROR
    open var assignable = false
    open var referencedVar: Variable? = null
}

class BinaryExpr(val operator: Operator, val lhs: Expr, val rhs: Expr,
                 location: Location) : Expr(location)

class UnaryExpr(val operator: Operator, val expr: Expr, location: Location) : Expr(location)

abstract class Atom(location: Location) : Expr(location)

class Cast(override var type: Type, val expr: Expr, location: Location) : Atom(location)

class FunctionCall(val name: String, val expr: List<Expr>, location: Location) : Atom(location)

class Graph(val gtype: GraphType, val num: Expr, val edges: List<Edge>,
            location: Location) : Atom(location)

class Identifier(val name: String, location: Location) : Atom(location)

class Literal(override var type: Type, val text: String, location: Location) : Atom(location)
