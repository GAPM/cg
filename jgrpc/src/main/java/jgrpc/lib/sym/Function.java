package jgrpc.lib.sym;

public class Function extends Symbol {
    public Type retType;

    public Function(String n, Type rt, String s, Location l) {
        super(n, s, l);
        retType = rt;
    }

    @Override
    public SymType getType() {
        return SymType.FUNC;
    }
}
