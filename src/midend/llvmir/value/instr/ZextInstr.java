package midend.llvmir.value.instr;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

public class ZextInstr extends Instr {

    public ZextInstr(String name, ValueType type, Value operand, BasicBlock belong) {
        super(name, type, InstrType.ZEXT, belong);
        super.addOperand(operand);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + operands.get(0).getValueType()
                + " " + operands.get(0).getName() + " to " + valueType;
    }
}
