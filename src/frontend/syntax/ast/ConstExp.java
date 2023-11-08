package frontend.syntax.ast;

import midend.llvmir.value.Value;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstExp extends Node {
    public ConstExp(ArrayList<Node> children) {
        super(SyntaxType.ConstExp, children);
    }

    @Override
    public String checkError() {
        return children.get(0).checkError();
    }

    public ArrayList<Integer> calculate() {
        return ((AddExp) children.get(0)).calculate();
    }

    @Override
    public Value genIR() {
        return children.get(0).genIR();
    }
}
