package frontend.semantics.symbol;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private final boolean isConst;                 // 常量标记
    private final ArrayList<Integer> dimensions;   // 维数、长度
    private ArrayList<Integer> values;             // 值

    public ArrayList<Integer> getDimensions() {
        return dimensions;
    }

    public int getValue(int index) {
        return values.get(index);
    }

    public boolean isConst() {
        return isConst;
    }

    public VarSymbol(boolean isConst, String name, ArrayList<Integer> dimensions, ArrayList<Integer> values) {
        this.isConst = isConst;
        this.name = name;
        this.dimensions = dimensions;
        this.values = (values != null) ? values : new ArrayList<>();
    }
}
