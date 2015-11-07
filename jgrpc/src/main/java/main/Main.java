package main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import java.util.Optional;

public class Main {
    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("c", false, "Just generate code");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        Optional<String> exec = GL.findExec("gcc");

        if (cmd.hasOption("c") || !exec.isPresent()) {
            System.err.println("WARNING: will only generate code");
        }
    }
}
