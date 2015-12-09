package jgrpc.lib.ex;

/**
 * Exception thrown to notify that syntax errors were found during parsing
 */
public class ParsingException extends Exception {
    public ParsingException() {
        super("There were errors during parsing");
    }
}
