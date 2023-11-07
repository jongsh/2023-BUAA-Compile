package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;

public class ZextInstr extends Instr {

    public ZextInstr(String name, ValueType type, Value operand, BasicBlock prev) {
        super(name, type, InstrType.ZEXT, prev);
        super.addOperand(operand);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + operands.get(0).getValueType()
                + " " + operands.get(0).getName() + " to " + valueType;
    }
}
