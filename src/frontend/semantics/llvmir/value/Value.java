package frontend.semantics.llvmir.value;

import frontend.semantics.llvmir.Use;
import frontend.semantics.llvmir.type.ValueType;

import java.util.ArrayList;

public class Value {
    protected String name;
    protected ValueType type;
    protected ArrayList<Use> useList;

    public Value(String name, ValueType type) {
        this.name = name;
        this.type = type;
        this.useList = new ArrayList<>();
    }

//    public Value() {
//        this.useList = new ArrayList<>();
//    }

}
