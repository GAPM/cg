package jgrpc.lib.symbol;

public class Variable extends Symbol {
    private Type type;

    public Variable(String n, Type t, String s, Location l) {
        super(n, s, l);
        type = t;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Variable) {
            Variable v = (Variable) o;
            if ((getName().equals(v.getName()) && type == v.getType())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public SymType getSymType() {
        return SymType.VAR;
    }
}
