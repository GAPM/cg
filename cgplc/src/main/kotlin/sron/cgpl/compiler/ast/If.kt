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

package sron.cgpl.compiler.ast

import sron.cgpl.compiler.State
import sron.cgpl.symbol.Location

class If(val cond: Expr, val stmts: List<Stmt>,
         val elifs: List<Elif>, val elsec: Else, location: Location) : Stmt(location) {

    override fun structure(s: State, func: FuncDef) {
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

        elsec.structure(s, func)
        elseReturns = elsec.returns

        returns = ifReturns && allElifsReturns && elseReturns
    }
}
