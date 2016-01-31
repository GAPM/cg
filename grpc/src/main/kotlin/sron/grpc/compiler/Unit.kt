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

package sron.grpc.compiler

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.antlr.v4.runtime.tree.ParseTreeWalker
import sron.grpc.compiler.internal.GrpLexer
import sron.grpc.compiler.internal.GrpParser
import sron.grpc.compiler.phase.*
import sron.grpc.exception.ParsingException
import sron.grpc.symbol.SymbolTable
import sron.grpc.util.Logger
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Path
import kotlin.reflect.KClass
import kotlin.system.measureTimeMillis

class Unit(fileName: String, private val paths: Array<Path>) {

    private val file = File(fileName)
    private val fis = FileInputStream(file)
    private val reader = InputStreamReader(fis, Charset.defaultCharset())
    private val input = ANTLRInputStream(reader)
    private val lexer = GrpLexer(input)
    private val tokens = CommonTokenStream(lexer)
    private val parser = GrpParser(tokens).withFileName(file.name)
    private val tree = parser.init()

    private val symTab = SymbolTable()
    private val results = ParseTreeProperty<Annotation>()

    var totalErrors = 0

    fun getNumberOfSyntaxErrors(): Int = parser.numberOfSyntaxErrors

    fun <T : Phase> executePhase(phaseClass: KClass<T>) {
        val walker = ParseTreeWalker()
        val phase = phaseClass.java.newInstance()

        phase.let {
            it.fileName = file.name
            it.symTab = symTab
            it.results = results
            it.paths = paths
        }

        val ms = measureTimeMillis { walker.walk(phase, tree) }

        phase.errorList.forEach { Logger.error(it) }
        totalErrors += phase.errorList.size

        Logger.debug("${file.name} [${phase.javaClass.name}]: $ms ms")
    }

    fun compileMyself() {
        if (parser.numberOfSyntaxErrors > 0) {
            throw ParsingException()
        }

        executePhase(Imports::class)
        executePhase(Globals::class)
        executePhase(Structure::class)
        executePhase(Types::class)
    }
}
