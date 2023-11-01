package frontend.semantics.symbol;

import frontend.semantics.llvmir.value.Value;

import java.util.ArrayList;

public class VarSymbol extends Symbol {
    private final boolean isConst;                 // 常量标记
    private final ArrayList<Integer> dimensions;   // 维数、长度
    private ArrayList<Integer> initials;             // 值
    private boolean isUndefined;                   // 全局变量初始化标志
    private Value LLVMValue;

    public ArrayList<Integer> getDimensions() {
        return dimensions;
    }

    public ArrayList<Integer> getInitials() {
        return initials;
    }

    public int getInitial(int index) {
        return initials.get(index);
    }

    public boolean isConst() {
        return isConst;
    }

    public void setLLVMValue(Value value) {
        this.LLVMValue = value;
    }

    public Value getLLVMValue() {
        return LLVMValue;
    }

    public VarSymbol(boolean isConst, String name, ArrayList<Integer> dimensions,
                     ArrayList<Integer> initials, boolean isUndefined) {
        this.isConst = isConst;
        this.name = name;
        this.dimensions = dimensions;
        this.initials = initials;
        this.isUndefined = isUndefined;
    }
}