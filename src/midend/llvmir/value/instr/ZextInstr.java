package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

import java.util.ArrayList;

public class ZextInstr extends Instr {

    public ZextInstr(String name, ValueType type, Value operand, BasicBlock belong) {
        super(name, type, InstrType.ZEXT, belong);
        super.addOperand(operand);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + operands.get(0).getValueType()
                + " " + operands.get(0).getName() + " to " + valueType;
    }

    @Override
    public void toMips() {
        IcmpInstr icmpInstr = (IcmpInstr) operands.get(0);
        String cond = icmpInstr.getIcmpCond();
        ArrayList<Value> IcmpOperands = icmpInstr.getIcmpOperand();
        MipsBuilder.getInstance().zextInstrToCmd(cond, this, IcmpOperands.get(0), IcmpOperands.get(1));
    }
}
