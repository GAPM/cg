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

package sron.grpc.compiler.phase

import sron.grpc.compiler.internal.GrpBaseListener
import sron.grpc.compiler.internal.GrpParser.*
import sron.grpc.compiler.nextId
import java.util.*

open class Scoper : GrpBaseListener() {
    protected val scope = Stack<String>()

    fun scopeUID() = scope.reduce { a, b -> "$a.$b" }

    /**
     * Updates the scope whenever the phase enters a function definition.
     */
    override fun enterFuncDef(ctx: FuncDefContext) {
        super.enterFuncDef(ctx)
        val name = ctx.Identifier().text
        scope.push(name)
    }

    /**
     * Updates the scope to `"global"` whenever the phase leaves a function
     * definition.
     */
    override fun exitFuncDef(ctx: FuncDefContext) {
        super.exitFuncDef(ctx)
        scope.pop()
    }

    override fun enterIfc(ctx: IfcContext) {
        super.enterIfc(ctx)
        scope.push("if${nextId()}")
    }

    override fun exitIfc(ctx: IfcContext) {
        super.exitIfc(ctx)

        if (scope.lastElement().startsWith("if")) {
            scope.pop()
        }
    }

    override fun enterElifc(ctx: ElifcContext) {
        super.enterElifc(ctx)

        if (scope.lastElement().startsWith("if")) {
            scope.pop()
        }

        scope.push("elif${nextId()}")
    }

    override fun exitElifc(ctx: ElifcContext) {
        super.exitElifc(ctx)
        scope.pop()
    }

    override fun enterElsec(ctx: ElsecContext) {
        super.enterElsec(ctx)

        if (scope.lastElement().startsWith("if")) {
            scope.pop()
        }

        scope.push("else${nextId()}")
    }

    override fun exitElsec(ctx: ElsecContext) {
        super.exitElsec(ctx)
        scope.pop()
    }

    override fun enterForc(ctx: ForcContext) {
        super.enterForc(ctx)
        scope.push("for${nextId()}")
    }

    override fun exitForc(ctx: ForcContext) {
        super.exitForc(ctx)
        scope.pop()
    }

    override fun enterWhilec(ctx: WhilecContext) {
        super.enterWhilec(ctx)
        scope.push("while${nextId()}")
    }

    override fun exitWhilec(ctx: WhilecContext) {
        super.exitWhilec(ctx)
        scope.pop()
    }
}
