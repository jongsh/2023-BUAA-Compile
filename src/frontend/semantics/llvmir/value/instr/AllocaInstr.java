package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.PointerType;
import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.BasicBlock;

public class AllocaInstr extends Instr {

    public AllocaInstr(String name, ValueType valueType, BasicBlock prev) {
        super(name, new PointerType(valueType), InstrType.ALLOCA, prev);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + ((PointerType) valueType).getTargetType();
    }
}
