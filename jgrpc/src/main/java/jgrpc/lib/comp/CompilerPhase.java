package jgrpc.lib.comp;

import jgrpc.lib.internal.GrpBaseListener;
import jgrpc.lib.sym.Location;
import jgrpc.lib.sym.SymTab;

import java.util.LinkedList;

public class CompilerPhase extends GrpBaseListener {
    protected SymTab symbolTable;
    protected String path;
    private LinkedList<String> errorList;

    public CompilerPhase() {
        errorList = new LinkedList<>();
    }

    public void setSymbolTable(SymTab symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void addError(Location location, String msg) {
        String e = String.format("%s:%s: %s", path, location, msg);
        errorList.add(e);
    }

    public int errorCount() {
        return errorList.size();
    }

    public LinkedList<String> getErrorList() {
        return errorList;
    }
}

