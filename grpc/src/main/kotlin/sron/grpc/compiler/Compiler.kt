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

package sron.grpc.compiler

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.antlr.v4.runtime.tree.ParseTreeWalker
import sron.grpc.compiler.internal.GrpLexer
import sron.grpc.compiler.internal.GrpParser
import sron.grpc.compiler.phase.*
import sron.grpc.exception.ErrorsInCodeException
import sron.grpc.exception.ParsingException
import sron.grpc.symbol.SymbolTable
import sron.grpc.util.Logger
import java.io.File
import kotlin.system.measureTimeMillis

class Compiler(fileName: String, val parameters: CompilerParameters) {
    private val file = File(fileName)
    lateinit private var tree: ParseTree

    private val symTab = SymbolTable()
    private val annotations = ParseTreeProperty<Annotation>()

    private var syntaxErrors = 0
    private var totalErrors = 0

    init {
        file.inputStream().use {
            val input = ANTLRInputStream(it)
            val lexer = GrpLexer(input)
            val tokens = CommonTokenStream(lexer)
            val parser = GrpParser(tokens).withFileName(file.name)
            tree = parser.init()
            syntaxErrors = parser.numberOfSyntaxErrors
        }
    }

    /**
     * Throws a [ParsingException] if the parser found syntax errors.
     */
    private fun checkParsing() {
        if (syntaxErrors > 0) {
            throw ParsingException()
        }
    }

    /**
     * Throws an [ErrorsInCodeException] if the total errors is higher than 0.
     */
    private fun checkForErrors() {
        if (totalErrors > 0) {
            throw ErrorsInCodeException()
        }
    }

    private fun <T : Phase> executePhase(constructor: () -> T) {
        val phase = constructor()
        val walker = ParseTreeWalker()

        with(phase) {
            fileName = this@Compiler.file.name
            symTab = this@Compiler.symTab
            annotations = this@Compiler.annotations
            parameters = this@Compiler.parameters
            init()
        }

        val ms = measureTimeMillis { walker.walk(phase, tree) }

        totalErrors += phase.errorList.size
        phase.errorList.forEach { Logger.error(it) }

        Logger.debug("${file.name} [${phase.javaClass.simpleName}]: $ms ms")
    }

    /**
     * Handles the compilation process.
     */
    fun compile() {
        checkParsing()

        executePhase(::Globals)
        executePhase(::Structure)
        executePhase(::Types)

        checkForErrors()

        executePhase(::PreGeneration)
        executePhase(::Generation)
    }
}
