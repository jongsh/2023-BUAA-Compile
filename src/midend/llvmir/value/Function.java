package midend.llvmir.value;

import backend.mips.MipsBuilder;
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

    public ArrayList<BasicBlock> getBasicBlockList() {
        return basicBlockList;
    }

    // 函数定义增加形参
    public void addParam(Param param) {
        this.paramList.add(param);
    }

    public ArrayList<Param> getParamList() {
        return paramList;
    }

    // 函数调用实参传值
    public void addArguments(ArrayList<Value> operands) {
        for (Value operand : operands) {
            super.addOperand(operand);
        }
    }

    public ArrayList<Value> getArguments() {
        return new ArrayList<>(operands);
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

    @Override
    public void toMips() {
        MipsBuilder.getInstance().addNoteCmd("\n# function: " + name);
        MipsBuilder.getInstance().addLabelCmd(name.substring(1));
        MipsBuilder.getInstance().allocaRegs(this);
        int i = (paramList.size() == 0) ? 0 : 1;
        for (; i < basicBlockList.size(); ++i) {
            basicBlockList.get(i).toMips();
        }
    }
}
