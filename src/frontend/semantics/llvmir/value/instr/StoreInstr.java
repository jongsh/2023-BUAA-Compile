package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.value.BasicBlock;

public class StoreInstr extends Instr {

    public StoreInstr(BasicBlock prev) {
        super("", null, InstrType.STORE, prev);
    }

    @Override
    public String toString() {
        return instrType + " " + operands.get(0) + ", " + operands.get(1);
    }
}
