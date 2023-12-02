package midend.llvmir.value.instr;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.User;
import midend.llvmir.value.Value;

import java.util.ArrayList;

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

    public boolean canBeDelete() {
        return true;
    }

    public String toGVNString() {
        return instrType.toString();
    }

    @Override
    public void toMips() {
    }
}
