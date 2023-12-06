package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

public class PCopyInstr extends Instr {
    public PCopyInstr(Value fromValue, Value toValue, BasicBlock belong) {
        super("", null, InstrType.PCOPY, belong);
        addOperand(fromValue);
        addOperand(toValue);
    }

    public Value getFromValue() {
        return operands.get(0);
    }

    public Value getToValue() {
        return operands.get(1);
    }

    public void modifyFromValue(Value newValue) {
        modifyOperand(operands.get(0), newValue, true);
    }

    @Override
    public String toString() {
        return instrType + " " + operands.get(0).getValueType() + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }

    @Override
    public void toMips() {
        MipsBuilder.getInstance().storeInstrToCmd(operands.get(0), operands.get(1));
    }
}
