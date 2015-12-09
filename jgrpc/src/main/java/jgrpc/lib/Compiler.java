package jgrpc.lib;

import jgrpc.lib.ex.GrpPhase;
import jgrpc.lib.ex.ParsingException;
import jgrpc.lib.internal.GrpLexer;
import jgrpc.lib.internal.GrpParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class Compiler {
    private GrpParser parser;
    private ParseTree tree;
    private boolean parsed;

    /**
     * Initialize the compiler for a single file
     *
     * @param path The path to the file to be compiled
     * @throws IOException if the file does not exists
     */
    public Compiler(String path) throws IOException, ParsingException {
        InputStream is = new FileInputStream(path);
        ANTLRInputStream input = new ANTLRInputStream(is);
        GrpLexer lexer = new GrpLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        parser = new GrpParser(tokens);
    }

    /**
     * Parse the file and fill the parse tree.
     *
     * @throws ParsingException if syntax errors are found in the file
     */
    public void parse() throws ParsingException {
        if (!parsed) {
            tree = parser.init();
            parsed = true;

            if (parser.getNumberOfSyntaxErrors() > 0) {
                throw new ParsingException();
            }
        }
    }

    public void executePhase(Class phase) {
        ParseTreeWalker walker = new ParseTreeWalker();
        Optional<GrpPhase> listener = Optional.empty();

        try {
            listener = Optional.of((GrpPhase) phase.newInstance());
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        walker.walk(listener.get(), tree);
    }
}
