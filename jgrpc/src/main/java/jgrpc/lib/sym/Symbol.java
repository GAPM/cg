package jgrpc.lib.sym;

public abstract class Symbol {
    public String name;
    public String scope;
    public abstract Type getType();
}
