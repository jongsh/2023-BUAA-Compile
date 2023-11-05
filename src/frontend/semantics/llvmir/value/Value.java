package frontend.semantics.llvmir.value;

import frontend.semantics.llvmir.Use;
import frontend.semantics.llvmir.type.ValueType;

import java.util.ArrayList;

public class Value {
    protected String name;
    protected ValueType valueType;
    protected ArrayList<Use> useList;

    public Value(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
        this.useList = new ArrayList<>();
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return valueType.toString() + " " + name;
    }
}
