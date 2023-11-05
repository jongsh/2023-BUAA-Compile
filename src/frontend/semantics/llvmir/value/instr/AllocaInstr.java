package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.BasicBlock;

public class AllocaInstr extends Instr {

    public AllocaInstr(String name, ValueType valueType, BasicBlock prev) {
        super(name, valueType, InstrType.ALLOCA, prev);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + valueType;
    }
}
