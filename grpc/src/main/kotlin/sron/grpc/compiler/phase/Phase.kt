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

import org.antlr.v4.runtime.tree.ParseTreeProperty
import sron.grpc.compiler.Annotation
import sron.grpc.compiler.CompilerParameters
import sron.grpc.compiler.Error
import sron.grpc.compiler.internal.GrpBaseListener
import sron.grpc.symbol.SymbolTable
import java.util.*

open class Phase : GrpBaseListener() {
    lateinit var fileName: String
    lateinit var symTab: SymbolTable
    lateinit var className: String
    lateinit var parameters: CompilerParameters

    lateinit var annotations: ParseTreeProperty<Annotation>
    val errorList = ArrayList<Error>()

    protected val scope = Stack<String>()

    fun init() {
        scope.push(fileName)
        className = fileName.substring(0, fileName.indexOf('.')).capitalize()
    }

    fun error(error: Error) = errorList.add(error)

    fun scopeUID() = scope.reduce { a, b -> "$a.$b" }
}
