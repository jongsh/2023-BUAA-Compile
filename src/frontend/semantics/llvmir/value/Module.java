package frontend.semantics.llvmir.value;

import frontend.semantics.llvmir.type.ValueType;

import java.util.ArrayList;

public class Module extends Value {
    private final String declareList = "declare i32 @getint()\ndeclare void @putint(i32)\n" +
            "declare void @putch(i32)\ndeclare void @putstr(i8*)\n\n";
    private ArrayList<GlobalVar> globalVarList;
    private ArrayList<GlobalStr> globalStrList;
    private ArrayList<Function> functionList;

    public Module() {
        super("module", new ValueType());
        this.globalVarList = new ArrayList<>();
        this.globalStrList = new ArrayList<>();
        this.functionList = new ArrayList<>();
    }

    public void addGlobalVar(GlobalVar globalVar) {
        this.globalVarList.add(globalVar);
    }

    public void addGlobalStr(GlobalStr globalStr) {
        this.globalStrList.add(globalStr);
    }

    public void addFunction(Function function) {
        this.functionList.add(function);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(declareList);
        for (GlobalVar globalVar : globalVarList) {
            sb.append(globalVar).append("\n");
        }
        sb.append("\n");
        for (Function function : functionList) {
            sb.append(function);
        }
        return sb.toString();
    }
}
