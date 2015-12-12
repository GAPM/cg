package jgrpc.lib.exception;

/**
 * Exception thrown to notify that syntax errors were found during parsing
 */
public class ParsingException extends CompilerException {
    public ParsingException() {
        super("There were errors during parsing");
    }
}
