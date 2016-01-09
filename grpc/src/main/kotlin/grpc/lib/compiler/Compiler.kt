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
import grpc.lib.compiler.phase.Globals
import grpc.lib.compiler.phase.Phase
import grpc.lib.compiler.phase.Structure
import grpc.lib.compiler.phase.Types
import grpc.lib.exception.ErrorsInCodeException
import grpc.lib.exception.ParsingException
import grpc.lib.symbol.SymbolTable
import grpc.lib.util.LogLevel
import grpc.lib.util.Logger
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.system.measureTimeMillis

class Compiler(path: String) {
    private val file = File(path)
    private val fis = FileInputStream(file)
    private val reader = InputStreamReader(fis, Charset.defaultCharset())
    private val input = ANTLRInputStream(reader)
    private val lexer = GrpLexer(input)
    private val tokens = CommonTokenStream(lexer)
    private val parser = GrpParser(tokens)
    private var totalErrors = 0

    private val symbolTable = SymbolTable()
    private val results = ParseTreeProperty<UnitResult>()

    /**
     * Throws a [ParsingException] if the parser found syntax errors.
     */
    private fun checkParsing() {
        if (parser.numberOfSyntaxErrors > 0) {
            throw ParsingException()
        }
    }

    /**
     * Throws an [ErrorsInCodeException] if the total errors is higher than 0.
     */
    private fun checkForErrors() {
        if (totalErrors > 0) {
            throw ErrorsInCodeException(totalErrors)
        }
    }

    /**
     * Executes a compilation phase.
     */
    private fun executePhase(tree: ParseTree, phase: Phase) {
        val walker = ParseTreeWalker()

        with(phase) {
            symTab = symbolTable
            fileName = file.name
            results = this@Compiler.results
        }

        val ms = measureTimeMillis { walker.walk(phase, tree) }

        if (phase.errorList.size > 0) {
            totalErrors += phase.errorList.size
            phase.errorList.forEach { Logger.log(it, LogLevel.ERROR) }
        }

        Logger.log("Phase ${phase.javaClass.name}: $ms millis", LogLevel.DEBUG)

        checkForErrors()
    }

    /**
     * Compiles a file.
     */
    fun compile() {
        val tree = parser.init()

        checkParsing()

        executePhase(tree, Structure())
        executePhase(tree, Globals())
        executePhase(tree, Types())
    }
}
