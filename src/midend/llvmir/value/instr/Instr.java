package midend.llvmir.value.instr;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.User;

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
