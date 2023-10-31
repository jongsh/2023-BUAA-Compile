package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class AddExp extends Node {
    public AddExp(ArrayList<Node> children) {
        super(SyntaxType.AddExp, children);
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
}
