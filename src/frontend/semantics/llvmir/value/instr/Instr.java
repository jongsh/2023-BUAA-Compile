package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.User;

public class Instr extends User {
    protected BasicBlock prev;
    protected InstrType instrType;

    public Instr(String name, ValueType valueType, InstrType instrType, BasicBlock prev) {
        super(name, valueType);
        this.prev = prev;
        this.instrType = instrType;
    }

    public BasicBlock getPrev() {
        return prev;
    }

    public InstrType getInstrType() {
        return instrType;
    }
}
