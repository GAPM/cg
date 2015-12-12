package grpc.lib.symbol;

import org.antlr.v4.runtime.tree.TerminalNode;

public class Location {
    private int lineno;
    private int columnno;

    public Location(int lineno, int columnno) {
        this.lineno = lineno;
        this.columnno = columnno;
    }

    public Location(TerminalNode tn) {
        this(tn.getSymbol().getLine(), tn.getSymbol().getCharPositionInLine());
    }

    @Override
    public String toString() {
        return String.format("%d:%d", lineno, columnno);
    }
}
