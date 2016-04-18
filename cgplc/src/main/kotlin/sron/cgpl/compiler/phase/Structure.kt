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

package sron.cgpl.compiler.phase

import sron.cgpl.compiler.Error
import sron.cgpl.compiler.State
import sron.cgpl.compiler.ast.*
import sron.cgpl.type.Type

object Structure {

    operator fun invoke(state: State, init: Init) = init.structure(state)

    private fun Init.structure(state: State) {
        for (fd in this.funcDef) {
            fd.structure(state)
        }
    }

    private fun FuncDef.structure(s: State) {
        var returns = false

        for (stmt in stmts) {
            stmt.structure(s, this)
            returns = returns || stmt.returns
        }

        if (type != Type.void && !returns) {
            s.errors += Error.notAllPathsReturn(location, name)
        }
    }

    private fun Stmt.structure(s: State, func: FuncDef) {
        when (this) {
            is If -> this.structure(s, func)
            is Return -> this.structure(s, func)
        }
    }

    private fun If.structure(s: State, func: FuncDef) {
        var ifReturns = false
        var allElifsReturns = true
        var elseReturns: Boolean

        for (stmt in stmts) {
            stmt.structure(s, func)
            ifReturns = ifReturns || stmt.returns
        }

        for (elif in elifs) {
            elif.structure(s, func)
            allElifsReturns = allElifsReturns && elif.returns
        }

        elsec?.structure(s, func)
        elseReturns = elsec?.returns ?: false

        returns = ifReturns && allElifsReturns && elseReturns
    }

    private fun Elif.structure(s: State, func: FuncDef) {
        for (stmt in stmts) {
            stmt.structure(s, func)
            returns = returns || stmt.returns
        }
    }

    private fun Else.structure(s: State, func: FuncDef) {
        for (stmt in stmts) {
            stmt.structure(s, func)
            returns = returns || stmt.returns
        }
    }

    private fun Return.structure(s: State, func: FuncDef) {
        if (func.type != Type.void && expr == null) {
            s.errors += Error.emptyReturn(location, func.name)
        }

        if (func.type == Type.void && expr != null) {
            s.errors += Error.nonEmptyReturn(location, func.name)
        }
    }
}
