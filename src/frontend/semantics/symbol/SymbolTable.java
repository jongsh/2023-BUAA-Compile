package frontend.semantics.symbol;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SymbolTable {
    public enum TableType {
        GLOBAL, MAIN_FUNC, FUNC, BLOCK, IF_BLOCK, FOR_BLOCK
    }

    private final TableType type;
    private final SymbolTable prev;
    private final ArrayList<SymbolTable> nexts;
    private final LinkedHashMap<String, Symbol> symbols;
    private final String funcName;   // FUNC、MAIN_FUNC 特有属性

    public SymbolTable(TableType type, SymbolTable prev) {
        this.type = type;
        this.prev = prev;
        this.symbols = new LinkedHashMap<>();
        this.nexts = new ArrayList<>();
        this.funcName = null;
    }

    public SymbolTable(TableType type, SymbolTable prev, String funcName) {
        this.type = type;
        this.prev = prev;
        this.symbols = new LinkedHashMap<>();
        this.nexts = new ArrayList<>();
        this.funcName = funcName;
    }

    public TableType getType() {
        return type;
    }

    public String getFuncName() {
        return funcName;
    }

    public void addNext(SymbolTable next) {
        this.nexts.add(next);
    }

    public SymbolTable getPrev() {
        return prev;
    }

    public SymbolTable getNext(int index) {
        return nexts.get(index);
    }

    public void addSymbol(Symbol symbol) {
        symbols.put(symbol.getName(), symbol);
    }

    public Symbol getSymbol(String name) {
        return symbols.get(name);
    }
}
