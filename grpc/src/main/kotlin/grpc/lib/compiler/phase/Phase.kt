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

package grpc.lib.compiler.phase

import grpc.lib.compiler.UnitResult
import grpc.lib.compiler.internal.GrpBaseListener
import grpc.lib.symbol.Location
import grpc.lib.symbol.SymbolTable
import org.antlr.v4.runtime.tree.ParseTreeProperty
import java.nio.file.Path
import java.util.*
import kotlin.properties.Delegates

open class Phase : GrpBaseListener() {
    var symTab: SymbolTable by Delegates.notNull()
    var fileName: String by Delegates.notNull()
    var results: ParseTreeProperty<UnitResult> by Delegates.notNull()
    var paths: Array<Path> by Delegates.notNull()
    val errorList = ArrayList<String>()

    fun addError(location: Location, msg: String) =
            errorList.add("$fileName:$location: $msg")

    fun Stack<String>.scopeStr(): String {
        var r = "$fileName.global"

        for (s in this) {
            r += ".$s"
        }

        return r
    }
}
