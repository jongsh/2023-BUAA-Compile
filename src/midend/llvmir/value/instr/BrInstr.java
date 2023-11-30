package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

import java.util.ArrayList;

public class BrInstr extends Instr {

    public BrInstr(BasicBlock obj, BasicBlock belong) {
        super("", new ValueType(), InstrType.BR, belong);
        super.addOperand(obj);
    }

    public BrInstr(Value condValue, BasicBlock trueObj, BasicBlock falseObj, BasicBlock belong) {
        super("", new ValueType(), InstrType.BR, belong);
        super.addOperand(condValue);
        super.addOperand(trueObj);
        super.addOperand(falseObj);
    }

    // 获得流图边
    public ArrayList<BasicBlock> getNextBlocks() {
        ArrayList<BasicBlock> ret = new ArrayList<>();
        int i = (operands.size() == 1) ? 0 : 1;
        for (; i < operands.size(); ++i) {
            ret.add((BasicBlock) operands.get(i));
        }
        return ret;
    }

    @Override
    public boolean canBeDelete() {
        return false;
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

    @Override
    public void toMips() {
        if (operands.size() == 1) {
            MipsBuilder.getInstance().addJumpCmd(operands.get(0).getName(), false);
        } else {
            IcmpInstr icmpInstr = (IcmpInstr) operands.get(0);
            String cond = icmpInstr.getIcmpCond();
            ArrayList<Value> IcmpOperands = icmpInstr.getIcmpOperand();
            String trueName = operands.get(1).getName();
            String falseName = operands.get(2).getName();
            MipsBuilder.getInstance().brInstrToCmd(cond, IcmpOperands.get(0), IcmpOperands.get(1), trueName, falseName);
        }
    }
}
