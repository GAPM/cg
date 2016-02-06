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

package sron.grpc.compiler

import sron.grpc.compiler.phase.*
import sron.grpc.exception.ErrorsInCodeException
import sron.grpc.exception.ParsingException
import java.nio.file.Paths
import kotlin.reflect.KClass

class Compiler(files: Array<String>) {
    private val paths = files.map { Paths.get(it) }.toTypedArray()
    private var totalErrors = 0

    private val units = files.map { Unit(it, paths) }

    /**
     * Throws a [ParsingException] if the parser found syntax errors.
     */
    private fun checkParsing() {
        val count = units.sumBy { it.getNumberOfSyntaxErrors() }
        if (count > 0) {
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

    fun <T : Phase> executePhaseForAll(phaseClass: KClass<T>) {
        units.forEach { it.executePhase(phaseClass) }
        totalErrors += units.sumBy { it.totalErrors }
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
        executePhaseForAll(Generation::class)
    }
}
