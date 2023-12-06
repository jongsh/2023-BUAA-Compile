package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
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

    @Override
    public void toMips() {
        MipsBuilder.getInstance().storeInstrToCmd(operands.get(0), this);
    }
}
