package midend.llvmir.value.instr;

import midend.llvmir.type.VarType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

public class IcmpInstr extends Instr {
    public enum IcmpType {
        ne, eq, slt, sgt, sle, sge;

        @Override
        public String toString() {
            return name();
        }
    }

    private final IcmpType icmpType;

    public IcmpInstr(String name, IcmpType icmpType, Value operand1, Value operand2, BasicBlock belong) {
        super(name, new VarType(1), InstrType.ICMP, belong);
        this.icmpType = icmpType;
        super.addOperand(operand1);
        super.addOperand(operand2);
    }

    @Override
    public String toString() {
        return name + " = " + instrType + " " + icmpType +" "+ operands.get(0).getValueType()
                + " " + operands.get(0).getName() + ", " + operands.get(1).getName();
    }

    // %3 = icmp eq i32 %2, 0
}
