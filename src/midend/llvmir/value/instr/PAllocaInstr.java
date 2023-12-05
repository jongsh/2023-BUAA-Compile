package midend.llvmir.value.instr;

import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

public class PAllocaInstr extends Instr {
    public PAllocaInstr(String name, Value fromValue, BasicBlock belong) {
        super(name, fromValue.getValueType(), InstrType.PALLOCA, belong);
        operands.add(fromValue);
    }

    @Override
    public String toString() {
        return instrType + " " + valueType + ", " + operands.get(0).getName() + " " + name;
    }
}
