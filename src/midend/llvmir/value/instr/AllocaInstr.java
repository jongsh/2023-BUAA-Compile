package midend.llvmir.value.instr;

import midend.llvmir.type.PointerType;
import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;

public class AllocaInstr extends Instr {

    public AllocaInstr(String name, ValueType valueType, BasicBlock prev) {
        super(name, new PointerType(valueType), InstrType.ALLOCA, prev);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + ((PointerType) valueType).getTargetType();
    }
}
