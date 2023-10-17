package frontend.semantics.Symbol;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final String name;
    private final SymbolTable prev;
    private final ArrayList<SymbolTable> next;
    private final HashMap<String, Symbol> symbols;

    public SymbolTable(String name, SymbolTable prev) {
        this.name = name;
        this.prev = prev;
        this.symbols = new HashMap<>();
        this.next = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void addNext(SymbolTable next) {
        this.next.add(next);
    }

    public SymbolTable getPrev() {
        return prev;
    }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    public Symbol getSymbol(String name, boolean isAll) {
        if (isAll && symbols.get(name) == null && prev != null) {
            return prev.getSymbol(name, true);
        }
        return symbols.get(name);
    }
}
