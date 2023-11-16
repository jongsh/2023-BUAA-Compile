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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!name.equals("")) {
            sb.append(name).append(" = ");
        }
        sb.append("call ").append(((Function) operands.get(0)).toCallerString());
        return sb.toString();
    }

    @Override
    public void toMips() {
        Function function = (Function) operands.get(0);
        ArrayList<Value> arguments = function.getArguments();
        MipsBuilder.getInstance().callInstrToCmd(function.getName().substring(1), arguments);
    }
}
