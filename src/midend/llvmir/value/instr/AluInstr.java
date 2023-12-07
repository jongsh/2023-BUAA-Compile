package midend.llvmir.value.instr;

import backend.mips.MipsBuilder;
import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Digit;
import midend.llvmir.value.Value;

public class AluInstr extends Instr {

    public AluInstr(String name, ValueType valueType, InstrType instrType, BasicBlock belong) {
        super(name, valueType, instrType, belong);
    }

    public void addOperands(Value operand1, Value operand2) {
        super.addOperand(operand1);
        super.addOperand(operand2);
    }

    public boolean simplify() {
        Value operand1 = operands.get(0);
        Value operand2 = operands.get(1);
        String op = (instrType.equals(InstrType.ADD)) ? "+" : (instrType.equals(InstrType.SUB)) ? "-" :
                (instrType.equals(InstrType.MUL)) ? "*" : (instrType.equals(InstrType.DIV)) ? "/" :
                        (instrType.equals(InstrType.MOD)) ? "%" : "";
        if (operand1 instanceof Digit && operand2 instanceof Digit) {
            Digit newDigit = Digit.calculate((Digit) operand1, (Digit) operand2, op);
            this.modifyToNewValue(newDigit);
            return true;
        } else if (operand1 instanceof AluInstr && operand2 instanceof Digit) {
            if (operand1.getUserList().size() == 1 && operand1.getUserList().contains(this)) {
                // 只被当前指令使用
                if (((AluInstr)operand1).mergeDigitToNewAlu(op, (Digit)operand2)) {
                    operand1.deleteUser(this);
                    this.modifyToNewValue(operand1);
                    return true;
                }
            }
        }
        return false;
    }

    // aluInstr op digit
    public boolean mergeDigitToNewAlu(String op, Digit digit) {
        String curOp = (instrType.equals(InstrType.ADD)) ? "+" : (instrType.equals(InstrType.SUB)) ? "-" :
                (instrType.equals(InstrType.MUL)) ? "*" : (instrType.equals(InstrType.DIV)) ? "/" :
                        (instrType.equals(InstrType.MOD)) ? "%" : "";
        if (operands.get(0) instanceof Digit) {
            Digit curDigit = (Digit) operands.get(0);
            if (op.equals("+")) {
                modifyOperand(curDigit, Digit.calculate(curDigit, digit, "+"), true);
                return true;
            } else if (op.equals("-")) {
                modifyOperand(curDigit, Digit.calculate(curDigit, digit, "-"), true);
                return true;
            }  else if (curOp.equals("*") && op.equals("*")) {
                modifyOperand(curDigit, Digit.calculate(curDigit, digit, "*"), true);
                return true;
            }
        } else if (operands.get(1) instanceof Digit) {
            Digit curDigit = (Digit) operands.get(1);
            if (curOp.equals("+") && op.equals("+")) {
                modifyOperand(curDigit, Digit.calculate(curDigit, digit, "+"), true);
                return true;
            } else if (curOp.equals("+") && op.equals("-")) {
                modifyOperand(curDigit, Digit.calculate(curDigit, digit, "-"), true);
                return true;
            } else if (curOp.equals("-") && op.equals("+")) {
                modifyOperand(curDigit, Digit.calculate(curDigit, digit, "-"), true);
                return true;
            } else if (curOp.equals("-") && op.equals("-")) {
                modifyOperand(curDigit, Digit.calculate(curDigit, digit, "+"), true);
                return true;
            }  else if (curOp.equals("*") && op.equals("*")) {
                modifyOperand(curDigit, Digit.calculate(curDigit, digit, "*"), true);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toGVNString() {
        String op = (instrType.equals(InstrType.ADD)) ? "add" : (instrType.equals(InstrType.SUB)) ? "sub" :
                (instrType.equals(InstrType.MUL)) ? "mul" : (instrType.equals(InstrType.DIV)) ? "sdiv" :
                        (instrType.equals(InstrType.MOD)) ? "srem" : "";
        StringBuilder sb = new StringBuilder(op + " " + valueType + " ");
        if (op.equals("add") || op.equals("mul")) {
            if (operands.get(0).getName().hashCode() < operands.get(1).getName().hashCode()) {
                sb.append(operands.get(0).getName()).append(operands.get(1).getName());
            } else {
                sb.append(operands.get(1).getName()).append(operands.get(0).getName());
            }
        } else {
            sb.append(operands.get(0).getName()).append(operands.get(1).getName());
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        String op = (instrType.equals(InstrType.ADD)) ? "add" : (instrType.equals(InstrType.SUB)) ? "sub" :
                (instrType.equals(InstrType.MUL)) ? "mul" : (instrType.equals(InstrType.DIV)) ? "sdiv" :
                        (instrType.equals(InstrType.MOD)) ? "srem" : "";
        return name + " = " + op + " " + valueType + " " +
                operands.get(0).getName() + ", " + operands.get(1).getName();
    }

    @Override
    public void toMips() {
        String op = (instrType.equals(InstrType.ADD)) ? "+" : (instrType.equals(InstrType.SUB)) ? "-" :
                (instrType.equals(InstrType.MUL)) ? "*" : (instrType.equals(InstrType.DIV)) ? "/" :
                        (instrType.equals(InstrType.MOD)) ? "%" : "";
        MipsBuilder.getInstance().aluInstrToCmd(op, this, operands.get(0), operands.get(1));
    }
}
