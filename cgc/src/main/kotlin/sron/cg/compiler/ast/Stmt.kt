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
import sron.cg.compiler.type.Type

enum class ControlType {
    CONTINUE,
    BREAK
}

abstract class Stmt(location: Location) : ASTNode(location) {
    var returns = false
    var scope = ""
}

class Assertion(val expr: Expr, location: Location): Stmt(location)

class Assignment(val lhs: Expr, val rhs: Expr, location: Location) : Stmt(location)

class Control(val type: ControlType, location: Location) : Stmt(location)

class Elif(val cond: Expr, val stmts: List<Stmt>, location: Location) : ASTNode(location) {
    var returns = false
    var scope = ""
}

class Else(val stmts: List<Stmt>, location: Location) : ASTNode(location) {
    var returns = false
    var scope = ""
}

class For(val initial: Assignment, val cond: Expr, val mod: Assignment,
          val stmts: Array<Stmt>, location: Location) : Stmt(location)

class If(val cond: Expr, val stmts: Array<Stmt>, val elifs: Array<Elif>,
         val elsec: Else?, location: Location) : Stmt(location)

class Print(val expr: Expr, location: Location) : Stmt(location)

class Return(val expr: Expr?, location: Location) : Stmt(location) {
    init {
        returns = true
    }
}

class VarDec(val name: String, val type: Type, val exp: Expr?,
             location: Location) : Stmt(location)

class While(val cond: Expr, val stmts: Array<Stmt>, location: Location) : Stmt(location)