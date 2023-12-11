package midend.llvmir.value;

import backend.mips.MipsBuilder;
import midend.llvmir.type.ValueType;

import java.util.ArrayList;

public class Module extends Value {
    private final String declareList = "declare i32 @getint()\ndeclare void @putint(i32)\n" +
            "declare void @putch(i32)\ndeclare void @putstr(i8*)\n";
    private final ArrayList<GlobalVar> globalVarList;
    private final ArrayList<GlobalStr> globalStrList;
    private final ArrayList<Function> functionList;

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

    public ArrayList<Function> getFunctionList() {
        return functionList;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(declareList);
        for (GlobalVar globalVar : globalVarList) {
            sb.append("\n").append(globalVar);
        }
        sb.append("\n");
        for (GlobalStr globalStr : globalStrList) {
            sb.append("\n").append(globalStr);
        }
        sb.append("\n");
        for (Function function : functionList) {
            sb.append("\n").append(function.toDefineString()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void toMips() {
        MipsBuilder.getInstance().allocaRegs(this);   // 首先分配寄存器
        for (GlobalVar globalVar : globalVarList) {
            globalVar.toMips();
        }
        for (GlobalStr globalStr : globalStrList) {
            globalStr.toMips();
        }
        functionList.get(functionList.size() - 1).toMips();  // main 函数
        for (int i = 0; i < functionList.size() - 1; i++) {
            functionList.get(i).toMips();
        }
        MipsBuilder.getInstance().addLabelCmd("end");
    }
}
