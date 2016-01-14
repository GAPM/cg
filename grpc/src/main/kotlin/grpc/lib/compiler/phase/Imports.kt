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

import grpc.lib.compiler.CompilationUnit
import grpc.lib.compiler.internal.GrpParser.ImportStmtContext
import grpc.lib.compiler.removeDoubleQuotes
import grpc.lib.symbol.Location
import java.io.File
import java.nio.file.Files

class Imports : Phase() {
    /**
     * Reports an import error caused by a file that could not be openned.
     */
    private fun importError(location: Location, fileName: String) =
            addError(location, "can not import file `$fileName`")

    /**
     * Reports that an imported file had compilation errors on it.
     */
    private fun importCompilationError(location: Location, fileName: String) =
            addError(location, "errors in imported file `$fileName`")

    /**
     * Creates a compilation unit from a file name.
     */
    private fun createCompilationUnit(fileName: String): CompilationUnit =
            CompilationUnit(fileName, symTab, results, paths)

    override fun exitImportStmt(ctx: ImportStmtContext) {
        super.exitImportStmt(ctx)

        val fileName = removeDoubleQuotes(ctx.StringLit().text)
        val file = File(fileName)
        val location = Location(ctx.start)

        if (file.exists() && !file.isDirectory) {
            val path = file.toPath()
            val duplicate = paths.find { Files.isSameFile(path, it) }

            if (duplicate == null) {
                val unit = createCompilationUnit(fileName)
                val errors = unit.compileMyself()

                if (errors > 0) {
                    importCompilationError(location, fileName)
                }
            }
        } else {
            importError(location, fileName)
        }
    }
}
