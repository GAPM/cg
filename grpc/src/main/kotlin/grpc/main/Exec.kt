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

package grpc.main

import grpc.lib.compiler.Compiler
import grpc.lib.exception.CompilerException
import grpc.lib.util.LogLevel
import grpc.lib.util.Logger
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.UnrecognizedOptionException
import java.io.IOException

fun main(args: Array<String>) {
    val options = Options()

    options.addOption("c", false, "Just generate code")
    options.addOption("d", "debug", false, "Print compiler debug messages")
    options.addOption("h", "help", false, "Show this help")

    val parser = DefaultParser()

    try {
        val cmd = parser.parse(options, args)

        if (cmd.hasOption("h")) {
            val hf = HelpFormatter()
            hf.printHelp("grpc [options] file", options)
            System.exit(0)
        }

        if (cmd.args.isEmpty()) {
            Logger.log("no input files", LogLevel.ERROR)
            System.exit(1)
        }

        if (cmd.hasOption("d")) {
            Logger.setMaxLevel(LogLevel.DEBUG)
        }

        try {
            val compiler = Compiler(cmd.args)

            try {
                compiler.compile()
            } catch(e: CompilerException) {
                Logger.error(e.message)
            }
        } catch(e: IOException) {
            Logger.error(e.message)
        }
    } catch (e: UnrecognizedOptionException) {
        Logger.error("unrecognized option ${e.option}")
    }
}
