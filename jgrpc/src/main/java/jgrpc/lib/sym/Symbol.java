package jgrpc.lib.sym;

public abstract class Symbol {
    public String name;
    public String scope;
    public Location location;
    public abstract SymType getType();

    public Symbol(String name, String scope, Location location) {
        this.name = name;
        this.scope = scope;
        this.location = location;
    }
}
