package midend.llvmir.value.instr;

import midend.llvmir.type.ArrayType;
import midend.llvmir.type.PointerType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

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
}
