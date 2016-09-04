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

package sron.cg.compiler

import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeWalker
import sron.cg.compiler.ast.Init
import sron.cg.compiler.exception.ErrorsInCodeException
import sron.cg.compiler.exception.ParsingException
import sron.cg.compiler.internal.CGLexer
import sron.cg.compiler.internal.CGParserCustom
import sron.cg.compiler.phase.*
import sron.cg.compiler.util.Logger
import java.io.File
import kotlin.system.measureTimeMillis

class Compiler(fileName: String, val parameters: Parameters) {
    companion object {
        private var _id = 0
        val nextID: Int
            get() = _id++
    }

    private val file = File(fileName)
    private val state = State(parameters)

    lateinit private var parser: CGParserCustom

    init {
        if (parameters.output == "") {
            parameters.output = file.nameWithoutExtension
        }

        file.inputStream().use {
            val input = ANTLRInputStream(it)
            val lexer = CGLexer(input)
            val tokens = CommonTokenStream(lexer)
            parser = CGParserCustom(file.name, tokens)
        }
    }

    private fun buildAST(tree: ParseTree): Init {
        val walker = ParseTreeWalker()
        val astGenerator = ASTSimplifier()
        walker.walk(astGenerator, tree)
        return astGenerator.getInit()
    }

    private fun executePhase(phase: Phase, init: Init) {
        val ms = measureTimeMillis {
            phase.execute(state, init)
        }

        Logger.debug("${phase.javaClass.simpleName}: $ms ms")
    }

    /**
     * Handles the compilation process.
     */
    fun compile() {
        var start: Long

        start = System.currentTimeMillis()
        val tree = parser.init()
        Logger.debug("Parse: ${System.currentTimeMillis() - start} ms")

        if (parser.numberOfSyntaxErrors > 0) {
            throw ParsingException()
        }

        start = System.currentTimeMillis()
        val ast = buildAST(tree)
        Logger.debug("AST: ${System.currentTimeMillis() - start} ms")

        executePhase(Globals, ast)
        executePhase(Structure, ast)
        executePhase(Types, ast)

        if (parameters.justCheck) {
            val msg = if (state.errors.size == 0) {
                "No errors found."
            } else {
                "${state.errors.size} error(s) found."
            }
            Logger.info("Check done. $msg")
        } else {
            if (state.errors.size > 0) {
                state.errors.forEach { Logger.error(it) }
                throw ErrorsInCodeException()
            }

            executePhase(Preparation, ast)
            executePhase(Generation, ast)
        }
    }
}
