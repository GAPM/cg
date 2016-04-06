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

package sron.cgpl.test

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.antlr.v4.runtime.tree.ParseTreeWalker
import sron.cgpl.compiler.Annotation
import sron.cgpl.compiler.CompilerParameters
import sron.cgpl.compiler.Error
import sron.cgpl.compiler.internal.GrpLexer
import sron.cgpl.compiler.internal.GrpParser
import sron.cgpl.compiler.phase.*
import sron.cgpl.exception.ErrorsInCodeException
import sron.cgpl.exception.ParsingException
import sron.cgpl.symbol.SymbolTable
import sron.cgpl.util.Logger
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import java.util.*

class TestCompiler(code: String) {
    private val parameters = CompilerParameters()
    val symTab = SymbolTable()
    val annotations = ParseTreeProperty<Annotation>()
    val errors = ArrayList<Error>()

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
        errors.addAll(phase.errorList)
        phase.errorList.forEach { Logger.error(it.message()) }
    }

    fun compile() {
        checkParsing()

        executePhase(::Globals)
        executePhase(::Structure)
        executePhase(::StaticCheck)

        checkForErrors()

        executePhase(::PreGeneration)
    }
}
