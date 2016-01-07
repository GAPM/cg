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
            hf.printHelp("grpc [options] files", options)
            System.exit(0)
        }

        if (cmd.args.isEmpty()) {
            Logger.log("no input files", LogLevel.ERROR)
            System.exit(1)
        }

        val fileName = cmd.args[0]

        try {
            val compiler = Compiler(fileName, cmd.hasOption("d"))

            try {
                compiler.compile()
            } catch(e: CompilerException) {
                Logger.log(e.message, LogLevel.ERROR)
            }
        } catch(_: IOException) {
            Logger.log("unable to open file $fileName", LogLevel.ERROR)
        }
    } catch (e: UnrecognizedOptionException) {
        Logger.log("unrecognized option ${e.option}", LogLevel.ERROR)
    }
}