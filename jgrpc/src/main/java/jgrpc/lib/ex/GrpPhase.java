package jgrpc.lib.ex;

import jgrpc.lib.internal.GrpBaseListener;

public class GrpPhase extends GrpBaseListener {
    public Object symbolTable;

    public void setSymbolTable(Object st) {
        symbolTable = st;
    }
}
