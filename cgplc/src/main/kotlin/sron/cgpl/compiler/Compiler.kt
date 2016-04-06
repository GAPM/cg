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

package sron.cgpl.compiler

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeProperty
import org.antlr.v4.runtime.tree.ParseTreeWalker
import sron.cgpl.compiler.internal.CGPLLexer
import sron.cgpl.compiler.internal.CGPLParser
import sron.cgpl.exception.ErrorsInCodeException
import sron.cgpl.exception.ParsingException
import sron.cgpl.symbol.SymbolTable
import sron.cgpl.util.Logger
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
            val lexer = CGPLLexer(input)
            val tokens = CommonTokenStream(lexer)
            val parser = CGPLParser(tokens).withFileName(file.name)
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

    /**
     * Handles the compilation process.
     */
    fun compile() {
        val walker = ParseTreeWalker()
        val converter = ToAST()

        val conversionTime = measureTimeMillis { walker.walk(converter, tree) }
        Logger.debug("Conversion from parse tree to AST: $conversionTime")
    }
}
