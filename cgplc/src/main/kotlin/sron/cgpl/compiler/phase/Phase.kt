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

import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ParseTreeProperty
import sron.cgpl.compiler.Annotation
import sron.cgpl.compiler.CompilerParameters
import sron.cgpl.compiler.Error
import sron.cgpl.symbol.SymbolTable
import sron.cgpl.type.Type
import java.util.*

open class Phase : Scoper() {
    lateinit var fileName: String
    lateinit var symTab: SymbolTable
    lateinit var parameters: CompilerParameters

    lateinit var annotations: ParseTreeProperty<Annotation>
    val errorList = ArrayList<Error>()

    fun init() {
        scope.push(fileName)
    }

    fun error(error: Error) = errorList.add(error)

    /**
     * Retrieves the type of a (sub)parse tree
     *
     * @param ctx The parse tree
     * @return The type of the parse tree, `Type.error` if it does not exists
     */
    fun getType(ctx: ParserRuleContext) = annotations.get(ctx)?.type ?: Type.ERROR

    /**
     * Sets the type of a (sub)parse tree, if the parse tree does not have an
     * entry in the result map, it's created
     *
     * @param ctx The parse tree
     * @param type The context of the type to be assigned
     */
    fun setType(ctx: ParserRuleContext, type: Type) {
        val r = annotations.get(ctx) ?: Annotation()
        r.type = type
        annotations.put(ctx, r)
    }

    /**
     * Returns whether a (sub)parse tree correspond to an assignable expression.
     *
     * @param ctx The parse tree
     */
    fun getAssignable(ctx: ParserRuleContext) = annotations.get(ctx)?.assignable ?: false

    /**
     * Sets whether a (sub) parse tree correspond to an assignable expression.
     * If the parse tree  does not have an entry in the result map, it's
     * created.
     *
     * @param ctx The parse tree
     * @param v `true` if it's assignable, `false` otherwise
     */
    fun setAssignable(ctx: ParserRuleContext, v: Boolean) {
        var r = annotations.get(ctx) ?: Annotation()
        r.assignable = v
        annotations.put(ctx, r)
    }

    /**
     *
     */
    fun getReturns(ctx: ParserRuleContext): Boolean {
        return annotations.get(ctx)?.returns ?: false
    }

    /**
     *
     */
    fun setReturns(ctx: ParserRuleContext, v: Boolean) {
        var r = annotations.get(ctx) ?: Annotation()
        r.returns = v
        annotations.put(ctx, r)
    }

    /**
     * Retrieves the map of variable indexes of a (sub)parse tree
     */
    fun getVarIndex(ctx: ParserRuleContext): MutableMap<String, Int> {
        return annotations.get(ctx)?.varIndex ?: mutableMapOf<String, Int>()
    }

    /**
     * Sets the map of variable indexes of a (sub)parse tree
     */
    fun setVarIndex(ctx: ParserRuleContext, varIndex: Map<String, Int>) {
        val r = annotations.get(ctx) ?: Annotation()
        r.varIndex.putAll(varIndex)
        annotations.put(ctx, r)
    }
}
