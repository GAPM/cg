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
import sron.cgpl.compiler.Annotation
import sron.cgpl.compiler.CompilerParameters
import sron.cgpl.compiler.Error
import sron.cgpl.compiler.internal.CGPLLexer
import sron.cgpl.compiler.internal.CGPLParser
import sron.cgpl.exception.ErrorsInCodeException
import sron.cgpl.exception.ParsingException
import sron.cgpl.symbol.SymbolTable
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
        val lexer = CGPLLexer(input)
        val tokens = CommonTokenStream(lexer)
        val parser = CGPLParser(tokens)

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

    fun compile() {
        checkParsing()

    }
}
