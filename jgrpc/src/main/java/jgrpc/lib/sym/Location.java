package jgrpc.lib.sym;

import org.antlr.v4.runtime.tree.TerminalNode;

public class Location {
    private int lineno;
    private int columnno;

    public Location(TerminalNode tn) {
        lineno = tn.getSymbol().getLine();
        columnno = tn.getSymbol().getCharPositionInLine();
    }

    @Override
    public String toString() {
        return String.format("%d:%d", lineno, columnno);
    }
}
