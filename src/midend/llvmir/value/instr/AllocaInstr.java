package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
import midend.llvmir.type.ArrayType;
import midend.llvmir.type.PointerType;
import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;

public class AllocaInstr extends Instr {

    public AllocaInstr(String name, ValueType valueType, BasicBlock belong) {
        super(name, new PointerType(valueType), InstrType.ALLOCA, belong);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + ((PointerType) valueType).getTargetType();
    }

    @Override
    public void toMips() {
        ValueType targetType = ((PointerType) valueType).getTargetType();
        if (targetType instanceof ArrayType) {
            MipsBuilder.getInstance().allocaInstrToCmd(this, targetType.size());
        }
    }
}
