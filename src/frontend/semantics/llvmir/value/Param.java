package frontend.semantics.llvmir.value;

import frontend.semantics.llvmir.type.ValueType;

public class Param extends Value {

    public Param(String name, ValueType type) {
        super(name, type);
    }

    @Override
    public String toString() {
        return type + " " + name;
    }
}
