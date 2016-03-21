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

import org.antlr.v4.runtime.ParserRuleContext
import sron.grpc.compiler.Annotation
import sron.grpc.compiler.internal.GrpParser.FdefContext

class PreGeneration : Phase() {
    var insideFunction = false
    var currentFunctionCtx: ParserRuleContext? = null
    var id = 0

    private fun getVarIndex(ctx: ParserRuleContext) = annotations.get(ctx)?.varIndex ?: mutableMapOf<String, Int>()

    private fun setVarIndex(ctx: ParserRuleContext, varIndex: Map<String, Int>) {
        val r = annotations.get(ctx) ?: Annotation()
        r.varIndex.putAll(varIndex)
        annotations.put(ctx, r)
    }

    override fun enterFdef(ctx: FdefContext) {
        super.enterFdef(ctx)
        insideFunction = true
        currentFunctionCtx = ctx

        val args = ctx.argList().arg()
        val index = getVarIndex(ctx)

        for (arg in args) {
            val name = arg.Identifier().text
            index[name] = id++
        }

        setVarIndex(ctx, index)
    }

    override fun exitFdef(ctx: FdefContext) {
        super.exitFdef(ctx)
        insideFunction = false
        currentFunctionCtx = null
        id = 0
    }
}
