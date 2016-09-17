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
import sron.cg.compiler.exception.ParsingException
import sron.cg.compiler.internal.CGLexer
import sron.cg.compiler.internal.CGParserCustom
import java.io.File

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

    /**
     * Handles the compilation process.
     */
    fun compile() {
        val parseTree = parser.unit()

        if (parser.numberOfSyntaxErrors > 0) {
            throw ParsingException()
        }
    }
}
