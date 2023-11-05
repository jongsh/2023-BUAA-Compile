package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ArrayType;
import frontend.semantics.llvmir.type.PointerType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;

public class GepInstr extends Instr {

    public GepInstr(String name, Value target, BasicBlock prev) {
        // target 是目标数组
        super(name, target.getValueType(), InstrType.GETELEMENTPTR, prev);
        super.addOperand(new Value(target.getName(), new PointerType(target.getValueType())));
    }

    public void addOperand(Value operand) {
        if (operands.size() > 1) {
            valueType = ((ArrayType) valueType).getEleType();
        }
        super.addOperand(operand);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = ").append(instrType).append(" ");
        sb.append(((PointerType) operands.get(0).getValueType()).getTargetType());
        for (Value operand : operands) {
            sb.append(", ").append(operand);
        }
        return sb.toString();
    }
}
