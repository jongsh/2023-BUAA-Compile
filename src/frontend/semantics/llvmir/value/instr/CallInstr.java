package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Function;

public class CallInstr extends Instr {

    public CallInstr(String name, Function function, BasicBlock prev) {
        // int函数有中间寄存器，void无
        super(name, function.getValueType(), InstrType.CALL, prev);
        super.addOperand(function);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!name.equals("")) {
            sb.append(name).append(" = ");
        }
        sb.append("call ").append(((Function) operands.get(0)).toCallerString());
        return sb.toString();
    }
}
