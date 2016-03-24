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

package sron.grpc.test

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.antlr.v4.runtime.tree.ParseTreeWalker
import sron.grpc.compiler.Annotation
import sron.grpc.compiler.CompilerParameters
import sron.grpc.compiler.internal.GrpLexer
import sron.grpc.compiler.internal.GrpParser
import sron.grpc.compiler.phase.*
import sron.grpc.exception.ErrorsInCodeException
import sron.grpc.exception.ParsingException
import sron.grpc.symbol.SymbolTable
import sron.grpc.util.Logger
import java.io.ByteArrayInputStream
import java.nio.charset.Charset

class TestCompiler(code: String) {
    private val parameters = CompilerParameters()
    val symTab = SymbolTable()
    val annotations = ParseTreeProperty<Annotation>()

    var syntaxErrors = 0
    var totalErrors = 0

    lateinit private var tree: ParseTree

    init {
        parameters.justCheck = true

        val stream = ByteArrayInputStream(code.toByteArray(Charset.defaultCharset()))
        val input = ANTLRInputStream(stream)
        val lexer = GrpLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = GrpParser(tokens)

        tree = parser.init()
        syntaxErrors = parser.numberOfSyntaxErrors
    }

    private fun checkParsing() {
        if (syntaxErrors > 0) {
            throw ParsingException()
        }
    }

    private fun checkForErrors() {
        if (totalErrors > 0) {
            throw ErrorsInCodeException()
        }
    }

    private fun <T : Phase> executePhase(constructor: () -> T) {
        val phase = constructor()
        val walker = ParseTreeWalker()

        with(phase) {
            fileName = "inlineCode"
            symTab = this@TestCompiler.symTab
            annotations = this@TestCompiler.annotations
            parameters = this@TestCompiler.parameters
            init()
        }

        walker.walk(phase, tree)

        totalErrors += phase.errorList.size
        phase.errorList.forEach { Logger.error(it) }
    }

    fun compile() {
        checkParsing()

        executePhase(::Globals)
        executePhase(::Structure)
        executePhase(::Types)

        checkForErrors()

        executePhase(::PreGeneration)
    }
}
