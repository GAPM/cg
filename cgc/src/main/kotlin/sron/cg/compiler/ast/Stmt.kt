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

import org.antlr.v4.runtime.Token
import sron.cg.compiler.internal.CGLexer
import sron.cg.compiler.lang.Type
import sron.cg.compiler.symbol.Scope

enum class ControlType {
    CONTINUE,
    BREAK;

    companion object {
        fun fromToken(tok: Token): ControlType = when (tok.type) {
            CGLexer.CONTINUE -> CONTINUE
            CGLexer.BREAK -> BREAK

            else -> throw IllegalStateException()
        }
    }
}

abstract class Stmt(location: Location) : Node(location) {
    var returns = false
    val funcDef by lazy {
        var fd = parent
        while (fd !is FuncDef) {
            fd = fd.parent
        }
        fd as FuncDef
    }
}

abstract class CompoundStmt(val body: List<Stmt>, location: Location) : Stmt(location) {

    override val scope by lazy {
        Scope(parent.scope, this.toString())
    }

    init {
        for (stmt in body) {
            stmt.parent = this
        }
    }
}

class VarDec(val id: String, val type: Type, val expr: Expr?,
             location: Location) : Stmt(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        expr?.parent = this
    }
}

class Assignment(val lhs: Expr, val rhs: Expr, location: Location) :
        Stmt(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        lhs.parent = this
        rhs.parent = this
    }
}

class Return(val expr: Expr?, location: Location) : Stmt(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        expr?.parent = this
        returns = true
    }
}

class Control(val type: ControlType, location: Location) : Stmt(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }
}

class Print(val expr: Expr, location: Location) : Stmt(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        expr.parent = this
    }
}

class Assert(val expr: Expr, location: Location) : Stmt(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        expr.parent = this
    }
}

class IfBlock(val ifc: If, val elif: List<Elif>, val elsec: Else,
              location: Location) : Stmt(location) {

    override val scope by lazy {
        Scope(parent.scope, null)
    }

    init {
        ifc.parent = this
        for (ei in elif) {
            ei.parent = this
        }
        elsec.parent = this
    }
}

class If(val expr: Expr, body: List<Stmt>, location: Location) : CompoundStmt(body, location) {
    init {
        expr.parent = this
    }
}

class Elif(val expr: Expr, body: List<Stmt>, location: Location) : CompoundStmt(body, location) {
    init {
        expr.parent = this
    }
}

class Else(body: List<Stmt>, location: Location) : CompoundStmt(body, location)

class For(val initial: Assignment, val condition: Expr,
          val modifier: Assignment, body: List<Stmt>, location: Location) :
        CompoundStmt(body, location) {
    init {
        initial.parent = this
        condition.parent = this
        modifier.parent = this
    }
}

class While(val condition: Expr, body: List<Stmt>, location: Location) :
        CompoundStmt(body, location) {
    init {
        condition.parent = this
        for (stmt in body) {
            stmt.parent = this
        }
    }
}
