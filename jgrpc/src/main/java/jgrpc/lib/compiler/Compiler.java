package jgrpc.lib.compiler;

import jgrpc.lib.exception.CompilerException;
import jgrpc.lib.exception.ParsingException;
import jgrpc.lib.internal.GrpLexer;
import jgrpc.lib.internal.GrpParser;
import jgrpc.lib.result.UnitResult;
import jgrpc.lib.symbol.SymTab;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

/**
 * jgrpc, the Java GRP compiler
 */
public class Compiler {
    private String path;
    private SymTab symTab;
    private GrpParser parser;
    private ParseTree tree;
    private ParseTreeProperty<UnitResult> results;

    /**
     * Initialize the compiler for a single file
     *
     * @param path The path to the file to be compiled
     * @throws IOException if the file does not exists
     */
    public Compiler(String path) throws IOException {
        this.path = path;
        symTab = new SymTab();
        results = new ParseTreeProperty<>();
        InputStream is = new FileInputStream(this.path);
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
    private void parse() throws ParsingException {
        tree = parser.init();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new ParsingException();
        }
    }

    /**
     * Executes a compilation phase
     *
     * @param phaseClass The class of the phase that is to be executed; must be
     *                   a subclass of CompilerPhase.
     */
    private void executePhase(Class phaseClass) {
        ParseTreeWalker walker = new ParseTreeWalker();
        Optional<CompilerPhase> listener = Optional.empty();

        try {
            listener = Optional.of((CompilerPhase) phaseClass.newInstance());
        } catch (ReflectiveOperationException | ClassCastException e) {
            System.err.println("Internal Error: Ill formed phase class");
        }

        if (listener.isPresent()) {
            CompilerPhase phase = listener.get();

            phase.setSymbolTable(symTab);
            phase.setResults(results);
            phase.setPath(path);

            walker.walk(phase, tree);

            if (phase.errorCount() > 0) {
                // System.err.println("\n" + phaseClass.getName() + ":");
                phase.getErrorList().forEach(System.err::println);
            }
        }
    }

    /**
     * Starts the compilation process
     *
     * @throws CompilerException if there is a problem during compilation
     */
    public void compile() throws CompilerException {
        parse();
        executePhase(GlobalDeclarations.class);
        executePhase(TypeChecking.class);
    }
}
