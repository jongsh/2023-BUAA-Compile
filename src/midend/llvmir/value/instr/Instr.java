package midend.llvmir.value.instr;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.User;

public class Instr extends User {
    protected BasicBlock belong;
    protected InstrType instrType;

    public Instr(String name, ValueType valueType, InstrType instrType, BasicBlock belong) {
        super(name, valueType);
        this.belong = belong;
        this.instrType = instrType;
    }

    public BasicBlock getBelong() {
        return belong;
    }

    public InstrType getInstrType() {
        return instrType;
    }

    @Override
    public void toMips() {
    }
}
