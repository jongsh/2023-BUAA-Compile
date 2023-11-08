package midend.llvmir.value;

import midend.llvmir.type.ValueType;

public class Param extends Value {

    public Param(String name, ValueType type) {
        super(name, type);
    }

    @Override
    public String toString() {
        return valueType + " " + name;
    }
}
