package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;

public class RetInstr extends Instr {

    public RetInstr(Value retValue, BasicBlock prev) {
        super("", new ValueType(), InstrType.RET, prev);
        super.addOperand(retValue);
    }

    @Override
    public String toString() {
        return instrType + " " + operands.get(0).getValueType() + " " + operands.get(0).getName();
    }
}
