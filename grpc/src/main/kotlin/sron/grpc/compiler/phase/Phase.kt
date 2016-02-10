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
import sron.grpc.compiler.internal.GrpBaseListener
import sron.grpc.symbol.Location
import sron.grpc.symbol.SymbolTable
import java.io.File
import java.util.*

open class Phase : GrpBaseListener() {
    lateinit var file: File
    lateinit var symTab: SymbolTable
    lateinit var annotations: ParseTreeProperty<Annotation>
    val errorList = ArrayList<String>()
    protected val scope = Stack<String>()

    fun init() {
        scope.push(file.name)
    }

    fun addError(location: Location, msg: String) {
        errorList.add("${file.name}:$location: $msg")
    }

    fun scopeUID() = scope.reduce { a, b -> "$a.$b" }
}
