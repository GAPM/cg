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

package sron.cg.compiler.phase

import sron.cg.compiler.Error
import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.type.Type

object Structure : Phase() {
    private lateinit var state: State

    private var insideLoop = false

    override fun execute(s: State, init: Init) {
        state = s
        init.structure()
    }

    private fun Init.structure() {
        for (fd in funcDef) {
            fd.structure()
        }
    }

    private fun FuncDef.structure() {
        var returns = false

        for (stmt in stmts) {
            stmt.structure(this)
            returns = returns || stmt.returns
        }

        if (type != Type.void && !returns) {
            state.errors += Error.notAllPathsReturn(location, name)
        }
    }

    private fun Stmt.structure(func: FuncDef) {
        when (this) {
            is If -> this.structure(func)
            is Return -> this.structure(func)
            is Control -> this.structure()
            is For -> this.structure(func)
            is While -> this.structure(func)
        }
    }

    private fun If.structure(func: FuncDef) {
        var ifReturns = false
        var allElifsReturns = true
        val elseReturns: Boolean

        for (stmt in stmts) {
            stmt.structure(func)
            ifReturns = ifReturns || stmt.returns
        }

        for (elif in elifs) {
            elif.structure(func)
            allElifsReturns = allElifsReturns && elif.returns
        }

        elsec?.structure(func)
        elseReturns = elsec?.returns ?: false

        returns = ifReturns && allElifsReturns && elseReturns
    }

    private fun Elif.structure(func: FuncDef) {
        for (stmt in stmts) {
            stmt.structure(func)
            returns = returns || stmt.returns
        }
    }

    private fun Else.structure(func: FuncDef) {
        for (stmt in stmts) {
            stmt.structure(func)
            returns = returns || stmt.returns
        }
    }

    private fun Return.structure(func: FuncDef) {
        if (func.type != Type.void && expr == null) {
            state.errors += Error.emptyReturn(location, func.name)
        }

        if (func.type == Type.void && expr != null) {
            state.errors += Error.nonEmptyReturn(location, func.name)
        }
    }

    private fun Control.structure() {
        if (!insideLoop) {
            state.errors += Error.controlNotInLoop(location, type)
        }
    }

    private fun For.structure(func: FuncDef) {
        insideLoop = true

        for (stmt in stmts) {
            stmt.structure(func)
        }

        insideLoop = false
    }

    private fun While.structure(func: FuncDef) {
        insideLoop = true

        for (stmt in stmts) {
            stmt.structure(func)
        }

        insideLoop = false
    }
}
