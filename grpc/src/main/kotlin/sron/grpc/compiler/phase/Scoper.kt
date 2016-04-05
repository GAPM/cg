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
import sron.grpc.compiler.internal.GrpParser.FuncDefContext
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
}
