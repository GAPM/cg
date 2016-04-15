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

import sron.cgpl.compiler.Error
import sron.cgpl.compiler.State
import sron.cgpl.symbol.Function
import sron.cgpl.symbol.Location
import sron.cgpl.symbol.SymType
import sron.cgpl.symbol.Variable
import sron.cgpl.type.Type

class FuncDef(val name: String, val type: Type, val args: List<Arg>,
              val stmts: List<Stmt>, location: Location) : ASTNode(location) {

    fun globals(s: State) {
        val qry = s.symbolTable.getSymbol(name, SymType.FUNC)

        if (qry == null) {
            for (arg in args) {
                val v = Variable(arg.name, arg.type, "global.$name", arg.location)
                s.symbolTable.addSymbol(v)
            }

            val func = Function(name, "global", type, location)
            s.symbolTable.addSymbol(func)
        } else {
            s.errors += Error.redeclaration(location, qry.location, name, SymType.FUNC)
        }
    }

    fun structure(s: State) {
        var returns = false

        for (stmt in stmts) {
            stmt.structure(s, this)
            returns = returns || stmt.returns
        }

        if (type != Type.void && !returns) {
            s.errors += Error.notAllPathsReturn(location, name)
        }
    }
}
