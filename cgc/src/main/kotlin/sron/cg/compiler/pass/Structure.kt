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

package sron.cg.compiler.pass

import sron.cg.compiler.State
import sron.cg.compiler.ast.*
import sron.cg.compiler.error.*
import sron.cg.compiler.lang.AtomType

class Structure(state: State) : Pass(state) {
    private var insideLoop = false

    private fun Control.structure() {
        if (!insideLoop) {
            state.errors += ControlNotInLoop(this)
        }
    }

    private fun Return.structure() {
        if (funcDef.type == AtomType.void && expr != null) {
            state.errors += NonEmptyReturnInVoidFunction(this, funcDef)
        }

        if (funcDef.type != AtomType.void && expr == null) {
            state.errors += EmptyReturnInNonVoidFunction(this, funcDef)
        }
    }

    private fun loop(body: List<Stmt>) {
        insideLoop = true

        for (stmt in body) {
            stmt.structure()
        }

        insideLoop = false
    }

    private fun For.structure() = loop(body)

    private fun While.structure() = loop(body)

    private fun analyzeBody(body: List<Stmt>): Boolean {
        var returns = false

        for (stmt in body) {
            stmt.structure()
            returns = returns || stmt.returns
        }

        return returns
    }

    private fun IfBlock.structure() {
        // Does the if block returns?
        ifc.returns = analyzeBody(ifc.body)

        // Does every elif block returns?
        elif.forEach { it.returns = analyzeBody(it.body) }
        val elifsReturns = elif.fold(true, { a, b -> a && b.returns })

        // Does the else block returns?
        val elseReturns = elsec?.let {
            analyzeBody(elsec.body)
        } ?: false

        returns = ifc.returns && elifsReturns && elseReturns
    }

    private fun Stmt.structure() {
        when (this) {
            is Control -> this.structure()
            is Return -> this.structure()
            is For -> this.structure()
            is While -> this.structure()
            is IfBlock -> this.structure()
        }
    }

    private fun FuncDef.structure() {
        val returns = analyzeBody(body)

        if (type != AtomType.void && !returns) {
            state.errors += MissingReturnStmtInFunction(this)
        }
    }

    override fun exec(ast: Init) {
        for (fd in ast.funcDef) {
            fd.structure()
        }
    }
}
