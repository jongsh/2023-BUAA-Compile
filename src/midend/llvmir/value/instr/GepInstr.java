package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
import midend.llvmir.type.ArrayType;
import midend.llvmir.type.PointerType;
import midend.llvmir.type.ValueType;
import midend.llvmir.type.VarType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

import java.util.ArrayList;

public class GepInstr extends Instr {

    public GepInstr(String name, Value target, BasicBlock belong) {
        // target 是目标数组
        super(name, target.getValueType(), InstrType.GETELEMENTPTR, belong);
        super.addOperand(target);
    }

    public void addOperand(Value operand) {
        if (operands.size() > 1) {
            valueType = ((ArrayType) ((PointerType) valueType).getTargetType()).toPointerType();
        }
        super.addOperand(operand);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = ").append(instrType).append(" ");
        sb.append(((PointerType) operands.get(0).getValueType()).getTargetType());
        for (Value operand : operands) {
            sb.append(", ").append(operand.getValueType()).append(" ").append(operand.getName());
        }
        return sb.toString();
    }

    @Override
    public void toMips() {
        ArrayList<Integer> dimensions = new ArrayList<>();
        ValueType tempType = operands.get(0).getValueType();
        while (((PointerType) tempType).getTargetType() instanceof ArrayType) {
            dimensions.add(((PointerType) tempType).getTargetType().size());
            tempType = ((ArrayType) ((PointerType) tempType).getTargetType()).toPointerType();
        }
        dimensions.add(1);
        // 数组元素单元长度
        int basicLength = ((VarType) ((PointerType) tempType).getTargetType()).getWidth() / 8;
        MipsBuilder.getInstance().gepInstrToCmd(this, operands.get(0), basicLength,
                new ArrayList<>(operands.subList(1, operands.size())), dimensions);
    }
}
