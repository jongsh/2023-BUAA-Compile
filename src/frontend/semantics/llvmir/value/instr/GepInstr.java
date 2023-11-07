package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ArrayType;
import frontend.semantics.llvmir.type.PointerType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;

public class GepInstr extends Instr {

    public GepInstr(String name, Value target, BasicBlock prev) {
        // target 是目标数组
        super(name, target.getValueType(), InstrType.GETELEMENTPTR, prev);
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
