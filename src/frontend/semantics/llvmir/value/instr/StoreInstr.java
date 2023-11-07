package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.PointerType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;

public class StoreInstr extends Instr {

    public StoreInstr(BasicBlock prev, Value from, Value to) {
        super("", null, InstrType.STORE, prev);
        super.addOperand(from);
        super.addOperand(to);


    }

    @Override
    public String toString() {
        return instrType + " " + operands.get(0).getValueType() + " " + operands.get(0).getName()
                + ", " + operands.get(1).getValueType() + " " + operands.get(1).getName();
    }
}
