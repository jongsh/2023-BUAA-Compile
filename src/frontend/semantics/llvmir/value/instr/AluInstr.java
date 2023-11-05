package frontend.semantics.llvmir.value.instr;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;

public class AluInstr extends Instr {

    public AluInstr(String name, ValueType valueType, InstrType instrType, BasicBlock prev) {
        super(name, valueType, instrType, prev);
    }

    public void addOperands(Value operand1, Value operand2) {
        super.addOperand(operand1);
        super.addOperand(operand2);
    }

    @Override
    public String toString() {
        String op = (instrType.equals(InstrType.ADD)) ? "add" : (instrType.equals(InstrType.SUB)) ? "sub" :
                (instrType.equals(InstrType.MUL)) ? "mul" : (instrType.equals(InstrType.DIV)) ? "sdiv" :
                        (instrType.equals(InstrType.MOD)) ? "srem" : "";
        return name + " = " + op + " " + valueType + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }
}
