package jgrpc.lib.symbol;

/**
 * Symbol class from which all other symbols in the symbol table inherit
 */
public abstract class Symbol {
    private String name;
    private String scope;
    private Location location;

    public Symbol(String name, String scope, Location location) {
        this.name = name;
        this.scope = scope;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getScope() {
        return scope;
    }

    /**
     * Every symbol must return their symbol type
     *
     * @return A value of the enum {@code SymType}
     */
    public abstract SymType getSymType();
}
