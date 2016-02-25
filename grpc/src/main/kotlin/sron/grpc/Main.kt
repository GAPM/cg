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

package sron.grpc

import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Options
import org.apache.commons.cli.UnrecognizedOptionException
import sron.grpc.compiler.Compiler
import sron.grpc.exception.CompilerException
import sron.grpc.util.Logger
import java.io.IOException

fun main(args: Array<String>) {
    val options = Options()

    options.addOption("c", false, "Just generate code")
    options.addOption("d", "debug", false, "Print compiler debug messages")
    options.addOption("h", "help", false, "Show this help")
    options.addOption("o", "output", true, "The compiler output")

    val cmdParser = DefaultParser()

    try {
        val cmd = cmdParser.parse(options, args)

        if (cmd.hasOption("h")) {
            val hf = HelpFormatter()
            hf.printHelp("grpc [options] file", options)
            System.exit(0)
        }

        if (cmd.args.isEmpty()) {
            Logger.error("no input files")
            System.exit(1)
        }

        if (cmd.hasOption("d")) {
            Logger.maxLevel = Logger.LogLevel.DEBUG
        }

        try {
            val compiler = Compiler(cmd.args[0])

            try {
                compiler.compile()
            } catch(e: CompilerException) {
                Logger.error(e.message)
                System.exit(1)
            }
        } catch(e: IOException) {
            Logger.error(e.message)
            System.exit(1)
        }
    } catch (e: UnrecognizedOptionException) {
        Logger.error("unrecognized option ${e.option}")
        System.exit(1)
    }
}
