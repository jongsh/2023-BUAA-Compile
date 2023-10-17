package frontend.semantics.Symbol;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private final boolean isConst;
    private final ArrayList<Integer> dimensions;
    private ArrayList<Integer> values;

    public ArrayList<Integer> getDimensions() {
        return dimensions;
    }

    public int getValue(int index) {
        return values.get(index);
    }

    public boolean isConst() {
        return isConst;
    }

    public VarSymbol(String name, ArrayList<Integer> dimensions, ArrayList<Integer> values) {
        this.name = name;
        this.isConst = true;
        this.values = values;
        this.dimensions = dimensions;
    }

    public VarSymbol(String name, ArrayList<Integer> dimensions) {
        this.name = name;
        this.isConst = false;
        this.values = null;
        this.dimensions = dimensions;
    }
}
