package jgrpc.lib.sym;

public class Function extends Symbol {
    @Override
    public Type getType() {
        return Type.FUNC;
    }

    public Function(String n, Type rt   ) {
        name = n;
        retType = rt;
    }

    public Type retType;
}
