package main;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import parser.Type;
import parser.internal.GrpLexer;
import parser.internal.GrpParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException {
        String s = "2+2/4**7**5*2/8+6-9;";
        InputStream is = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));

        ANTLRInputStream input = new ANTLRInputStream(is);
        GrpLexer lexer = new GrpLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GrpParser parser = new GrpParser(tokens);

        Type a = Type.type_void;
        System.out.println(a);
    }
}
