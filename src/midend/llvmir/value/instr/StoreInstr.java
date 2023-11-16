package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

public class StoreInstr extends Instr {

    public StoreInstr(BasicBlock belong, Value from, Value to) {
        super("", null, InstrType.STORE, belong);
        super.addOperand(from);
        super.addOperand(to);
    }

    @Override
    public String toString() {
        return instrType + " " + operands.get(0).getValueType() + " " + operands.get(0).getName()
                + ", " + operands.get(1).getValueType() + " " + operands.get(1).getName();
    }

    @Override
    public void toMips() {
        MipsBuilder.getInstance().storeInstrToCmd(operands.get(0), operands.get(1));
    }
}
