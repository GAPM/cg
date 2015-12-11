package jgrpc.lib.sym;

public class Variable extends Symbol {
    public Type type;

    public Variable(String n, Type t, String s, Location l) {
        super(n, s, l);
        type = t;
    }

    @Override
    public SymType getType() {
        return SymType.VAR;
    }
}
