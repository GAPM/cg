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

package grpc.lib.compiler

import grpc.lib.compiler.internal.GrpLexer
import grpc.lib.compiler.internal.GrpParser
import grpc.lib.compiler.phase.*
import grpc.lib.symbol.SymbolTable
import grpc.lib.util.Logger
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.system.measureTimeMillis

class CompilationUnit(fileName: String, private val symTab: SymbolTable,
                      private val results: ParseTreeProperty<UnitResult>,
                      private val paths: Array<Path>) {
    private val file = File(fileName)
    private val fis = FileInputStream(file)
    private val reader = InputStreamReader(fis, Charset.defaultCharset())
    private val input = ANTLRInputStream(reader)
    private val lexer = GrpLexer(input)
    private val tokens = CommonTokenStream(lexer)
    private val parser = GrpParser(tokens)
    private val tree = parser.init()

    fun getNumberOfSyntaxErrors(): Int = parser.numberOfSyntaxErrors

    fun <T : Phase> executePhase(phaseClass: KClass<T>): Int {
        val walker = ParseTreeWalker()
        val phase = phaseClass.java.newInstance()

        with (phase) {
            this.fileName = file.name
            this.symTab = this@CompilationUnit.symTab
            this.results = this@CompilationUnit.results
            this.paths = this@CompilationUnit.paths
        }

        val ms = measureTimeMillis { walker.walk(phase, tree) }

        phase.errorList.forEach { Logger.error(it) }

        Logger.debug("${file.name} [${phase.javaClass.name}]: $ms ms")

        return phase.errorList.size
    }

    fun compileMyself(): Int {
        if (parser.numberOfSyntaxErrors > 0) {
            return 1
        }

        var errors = 0

        errors += executePhase(Imports::class)
        errors += executePhase(Globals::class)
        errors += executePhase(Structure::class)
        errors += executePhase(Types::class)

        return errors
    }
}
