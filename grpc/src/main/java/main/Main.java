package main;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import parser.Listener;
import parser.internal.GrpLexer;
import parser.internal.GrpParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        String s = "2+2;";
        InputStream is = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));

        ANTLRInputStream input = new ANTLRInputStream(is);
        GrpLexer lexer = new GrpLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GrpParser parser = new GrpParser(tokens);

        ParseTree tree = parser.init();
        ParseTreeWalker walker = new ParseTreeWalker();
        Listener listener = new Listener();

        walker.walk(listener, tree);
    }
}
