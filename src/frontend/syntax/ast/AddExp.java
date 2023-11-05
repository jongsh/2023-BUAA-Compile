package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.Digit;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.llvmir.value.instr.AluInstr;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class AddExp extends Node {
    public AddExp(ArrayList<Node> children) {
        super(SyntaxType.AddExp, children);
    }

    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();
        if (children.size() > 1) {
            int num1 = ((AddExp) children.get(0)).calculate().get(0);
            int num2 = ((MulExp) children.get(2)).calculate().get(0);
            int value = (children.get(1).getType().equals(SyntaxType.MINU)) ? num1 - num2 : num1 + num2;
            values.add(value);
        } else {
            values.add(((MulExp) children.get(0)).calculate().get(0));
        }
        return values;
    }

    // AddExp → MulExp | AddExp ('+' | '−') MulExp
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }

    @Override
    public Value genIR() {
        Value retValue = children.get(0).genIR();
        Value tempValue;
        AluInstr aluInstr;
        for (int i = 1; i < children.size(); i += 2) {
            String aluType = (children.get(i).getType().equals(SyntaxType.MINU)) ? "-" : "+";
            tempValue = children.get(i + 1).genIR();
            if (retValue instanceof Digit && tempValue instanceof Digit) {
                retValue = Digit.calculate((Digit) retValue, (Digit) tempValue, aluType);
            } else {
                aluInstr = IRBuilder.getInstance().newAluInstr(aluType);
                aluInstr.addOperands(retValue, tempValue);
                IRBuilder.getInstance().addInstr(aluInstr);
                retValue = aluInstr;
            }
        }
        return retValue;
    }
}
