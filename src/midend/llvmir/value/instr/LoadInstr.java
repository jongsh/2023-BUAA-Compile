package midend.llvmir.value.instr;

import midend.llvmir.type.PointerType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

public class LoadInstr extends Instr {

    public LoadInstr(String name, Value target, BasicBlock belong) {
        super(name, ((PointerType) target.getValueType()).getTargetType(), InstrType.LOAD, belong);
        super.addOperand(target);
    }


    @Override
    public String toString() {
        return name + " = " + instrType + " " + valueType + ", " +
                operands.get(0).getValueType() + " " + operands.get(0).getName();
    }
}
