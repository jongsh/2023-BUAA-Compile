package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.PointerType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;

public class LoadInstr extends Instr {

    public LoadInstr(String name, Value target, BasicBlock prev) {
        super(name, ((PointerType) target.getValueType()).getTargetType(), InstrType.LOAD, prev);
        super.addOperand(target);
    }


    @Override
    public String toString() {
        return name + " = " + instrType + " " + valueType + ", " +
                operands.get(0).getValueType() + " " + operands.get(0).getName();
    }
}
