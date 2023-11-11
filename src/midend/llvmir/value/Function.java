package midend.llvmir.value;

import midend.llvmir.type.ValueType;

import java.util.ArrayList;

public class Function extends User {
    private final Module belong;
    private final ArrayList<Param> paramList;
    private final ArrayList<BasicBlock> basicBlockList;

    public Function(String name, ValueType type, Module belong) {
        super(name, type);
        this.belong = belong;
        this.basicBlockList = new ArrayList<>();
        this.paramList = new ArrayList<>();
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        this.basicBlockList.add(basicBlock);
    }

    public void addParam(Param param) {
        this.paramList.add(param);
    }

    public void addOperands(ArrayList<Value> operands) {
        for (Value operand : operands) {
            super.addOperand(operand);
        }
    }

    public String toCallerString() {
        StringBuilder sb = new StringBuilder();
        sb.append(valueType).append(" ").append(name);
        sb.append("(");
        for (Value operand : operands) {
            sb.append(operand.getValueType()).append(" ").append(operand.getName()).append(", ");
        }
        if (operands.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }


    public String toDefineString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(valueType).append(" ").append(name);
        sb.append("(");
        for (Param param : paramList) {
            sb.append(param).append(", ");
        }
        if (paramList.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(") {");
        for (BasicBlock basicBlock : basicBlockList) {
            sb.append(basicBlock);
        }
        sb.append("}");
        return sb.toString();
    }
}
