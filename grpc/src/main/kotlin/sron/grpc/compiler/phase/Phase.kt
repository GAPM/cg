/*
 * Copyright 2016 Simón Oroño
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
import java.nio.file.Path
import java.util.*
import kotlin.properties.Delegates

open class Phase : GrpBaseListener() {
    var symTab: SymbolTable by Delegates.notNull()
    var fileName: String by Delegates.notNull()
    var results: ParseTreeProperty<Annotation> by Delegates.notNull()
    var paths: Array<Path> by Delegates.notNull()
    val errorList = ArrayList<String>()
    protected val scope = Stack<String>()

    fun addError(location: Location, msg: String) =
            errorList.add("$fileName:$location: $msg")

    fun Stack<String>.str(): String {
        var r = "$fileName.global"

        for (s in this) {
            r += ".$s"
        }

        return r
    }
}