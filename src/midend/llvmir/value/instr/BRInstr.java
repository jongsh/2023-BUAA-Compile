package midend.llvmir.value.instr;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

public class BRInstr extends Instr {

    public BRInstr(BasicBlock obj, BasicBlock prev) {
        super("", new ValueType(), InstrType.BR, prev);
        super.addOperand(obj);
    }

    public BRInstr(Value condValue, BasicBlock trueObj, BasicBlock falseObj, BasicBlock prev) {
        super("", new ValueType(), InstrType.BR, prev);
        super.addOperand(condValue);
        super.addOperand(trueObj);
        super.addOperand(falseObj);
    }

    @Override
    public String toString() {
        if (operands.size() > 1) {
            return instrType + " " + operands.get(0).getValueType() + " " + operands.get(0).getName()
                    + ", label %" + operands.get(1).getName() + ", label %" + operands.get(2).getName();
        } else {
            return instrType + " label %" + operands.get(0).getName();
        }
    }
}
