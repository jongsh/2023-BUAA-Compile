package midend.llvmir.value.instr;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

public class RetInstr extends Instr {

    public RetInstr(Value retValue, BasicBlock belong) {
        super("", new ValueType(), InstrType.RET, belong);
        super.addOperand(retValue);
    }

    @Override
    public String toString() {
        return instrType + " " + operands.get(0).getValueType() + " " + operands.get(0).getName();
    }
}