package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.type.VarType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;

public class IcmpInstr extends Instr {
    public enum IcmpType {
        ne, eq, slt, sgt, sle, sge;

        @Override
        public String toString() {
            return name();
        }
    }

    private IcmpType icmpType;

    public IcmpInstr(String name, IcmpType icmpType, Value operand1, Value operand2, BasicBlock prev) {
        super(name, new VarType(1), InstrType.ICMP, prev);
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
