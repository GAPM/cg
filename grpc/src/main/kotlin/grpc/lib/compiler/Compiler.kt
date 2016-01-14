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

import grpc.lib.compiler.phase.*
import grpc.lib.exception.ErrorsInCodeException
import grpc.lib.exception.ParsingException
import grpc.lib.symbol.SymbolTable
import org.antlr.v4.runtime.tree.ParseTreeProperty
import java.nio.file.Paths
import kotlin.reflect.KClass

class Compiler(files: Array<String>) {
    private val paths = files.map { Paths.get(it) }.toTypedArray()
    private var totalErrors = 0

    private val symbolTable = SymbolTable()
    private val results = ParseTreeProperty<UnitResult>()

    private val compUnits = files.map {
        CompilationUnit(it, symbolTable, results, paths)
    }

    /**
     * Throws a [ParsingException] if the parser found syntax errors.
     */
    private fun checkParsing() {
        val count = compUnits.map { it.getNumberOfSyntaxErrors() }.sum()
        if (count > 0) {
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

    fun <T : Phase> executePhaseForAll(phaseClass: KClass<T>) {
        compUnits.map {
            totalErrors += it.executePhase(phaseClass)
        }

        checkForErrors()
    }

    /**
     * Handles the compilation process.
     */
    fun compile() {
        checkParsing()

        executePhaseForAll(Imports::class)
        executePhaseForAll(Globals::class)
        executePhaseForAll(Structure::class)
        executePhaseForAll(Types::class)
    }
}
