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
import org.antlr.v4.runtime.tree.ParseTreeWalker
import sron.cgpl.compiler.ast.Init
import sron.cgpl.compiler.internal.CGPLLexer
import sron.cgpl.compiler.internal.CGPLParser
import sron.cgpl.compiler.phase.Globals
import sron.cgpl.compiler.phase.Structure
import sron.cgpl.exception.ErrorsInCodeException
import sron.cgpl.exception.ParsingException
import sron.cgpl.util.Logger
import java.io.File

class Compiler(fileName: String, val parameters: Parameters) {
    private val file = File(fileName)

    private val state = State(parameters)

    lateinit private var parser: CGPLParser

    init {
        file.inputStream().use {
            val input = ANTLRInputStream(it)
            val lexer = CGPLLexer(input)
            val tokens = CommonTokenStream(lexer)
            parser = CGPLParser(tokens).withFileName(file.name)
        }
    }

    /**
     * Handles the compilation process.
     */
    fun compile() {
        val tree = parser.init()
        if (parser.numberOfSyntaxErrors > 0) {
            throw ParsingException()
        }

        val walker = ParseTreeWalker()
        val converter = ToAST()
        val ast: Init

        measureTime("To AST", { walker.walk(converter, tree) })

        ast = converter.getResult()

        measureTime("Globals", { Globals(state, ast) })
        measureTime("Structure", { Structure(state, ast) })

        if (state.errors.size > 0) {
            state.errors.forEach { Logger.error(it) }
            throw ErrorsInCodeException()
        }
    }
}
