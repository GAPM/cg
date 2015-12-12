package grpc.lib.symbol;

import java.util.ArrayList;
import java.util.Optional;

public class SymTab {
    private ArrayList<Symbol> symTab;

    public SymTab() {
        symTab = new ArrayList<>();
    }

    /**
     * Gets a symbol with matching name, scope and type.
     *
     * @param name  the name to be matched
     * @param scope the scope to be matched
     * @param type  the type to be matched
     * @return a symbol with matching name, scope and type if its found
     */
    public Optional<Symbol> getSymbol(String name, String scope, SymType type) {
        return symTab.stream()
                .filter(s -> s.getName().equals(name))
                .filter(s -> s.getScope().equals(scope))
                .filter(s -> s.getSymType() == type || type == SymType.ANY)
                .findFirst();
    }

    public Symbol[] getSymbols(String name, SymType type) {
        return symTab.stream()
                .filter(s -> s.getName().equals(name))
                .filter(s -> s.getSymType() == type || type == SymType.ANY)
                .toArray(Symbol[]::new);
    }

    public void addSymbol(Symbol s) {
        symTab.add(s);
    }
}
