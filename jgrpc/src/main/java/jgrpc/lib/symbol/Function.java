package jgrpc.lib.symbol;

import java.util.LinkedList;

public class Function extends Symbol {
    private Type retType;
    private LinkedList<Variable> args;

    public Function(String name, Type retType, Location location, LinkedList<Variable> args) {
        super(name, "global", location);
        this.retType = retType;
        this.args = args;
    }

    @Override
    public SymType getSymType() {
        return SymType.FUNC;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Function) {
            Function f = (Function) o;

            // Checks name, and return type
            if (!(getName().equals(f.getName()) && retType == f.retType)) {
                return false;
            }

            if (args.size() == f.args.size()) {
                for (int i = 0; i < args.size(); ++i) {
                    if (args.get(i).getType() != f.args.get(i).getType()) {
                        return false;
                    }
                }
            } else {
                return false;
            }

            return true;
        }

        return false;
    }
}
