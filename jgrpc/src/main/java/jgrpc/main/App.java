package jgrpc.main;

import jdk.nashorn.internal.codegen.CompilationException;
import jgrpc.lib.comp.Compiler;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.io.File;
import java.util.Optional;

class App {
    private static Optional<String> findExec(String name) {
        String path = System.getenv("PATH");
        String[] dirs = path.split(File.pathSeparator);
        File f;

        for (String dir : dirs) {
            f = new File(dir, name);
            if (f.exists() && !f.isDirectory() && f.canExecute()) {
                return Optional.of(f.getAbsolutePath());
            }
        }
        return Optional.empty();
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("c", false, "Just generate code");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        Optional<String> exec = findExec("clang");

        if (cmd.hasOption("c") || !exec.isPresent()) {
            System.err.println("WARNING: will only generate code");
        }

        if (cmd.getArgs().length == 0) {
            System.err.println("ERROR: no input file");
            System.exit(1);
        }

        Compiler compiler = new Compiler(cmd.getArgs()[0]);

        try {
            compiler.compile();
        } catch (CompilationException e) {
            System.err.println(e.getMessage());
        }
    }
}
