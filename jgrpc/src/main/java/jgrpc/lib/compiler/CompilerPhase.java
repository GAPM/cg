package jgrpc.lib.compiler;

import jgrpc.lib.internal.GrpBaseListener;
import jgrpc.lib.result.UnitResult;
import jgrpc.lib.symbol.Location;
import jgrpc.lib.symbol.SymTab;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.util.LinkedList;

public class CompilerPhase extends GrpBaseListener {
    protected SymTab symbolTable;
    protected String path;
    private LinkedList<String> errorList;
    private ParseTreeProperty<UnitResult> results;

    public CompilerPhase() {
        errorList = new LinkedList<>();
    }

    public void setSymbolTable(SymTab symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void setResults(ParseTreeProperty<UnitResult> results) {
        this.results = results;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addError(Location location, String msg) {
        String e = String.format("%s:%s: error: %s", path, location, msg);
        errorList.add(e);
    }

    public int errorCount() {
        return errorList.size();
    }

    public LinkedList<String> getErrorList() {
        return errorList;
    }
}

