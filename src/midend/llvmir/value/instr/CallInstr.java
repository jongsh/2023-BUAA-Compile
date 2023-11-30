package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Function;
import midend.llvmir.value.Value;

import java.util.ArrayList;

public class CallInstr extends Instr {

    public CallInstr(String name, Function function, BasicBlock belong) {
        // int函数有中间寄存器，void无
        super(name, function.getValueType(), InstrType.CALL, belong);
        super.addOperand(function);
    }

    public void addArguments(ArrayList<Value> operands) {
        for (Value operand : operands) {
            super.addOperand(operand);
        }
    }

    @Override
    public boolean canBeDelete() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!name.equals("")) {
            sb.append(name).append(" = ");
        }
        sb.append("call ").append(((Function) operands.get(0)).toCallerString());
        sb.append("(");
        for (int i = 1; i < operands.size(); ++i) {
            sb.append(operands.get(i).getValueType()).append(" ").append(operands.get(i).getName()).append(", ");
        }
        if (operands.size() > 1) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public void toMips() {
        Function function = (Function) operands.get(0);
        ArrayList<Value> arguments = new ArrayList<>(operands.subList(1, operands.size()));
        MipsBuilder.getInstance().callInstrToCmd(this, function.getName().substring(1), arguments);
    }
}
