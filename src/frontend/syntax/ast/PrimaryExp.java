package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class PrimaryExp extends Node {
    public PrimaryExp(ArrayList<Node> children) {
        super(SyntaxType.PrimaryExp, children);
    }

    // PrimaryExp → '(' Exp ')' | LVal | Number
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        if (children.size() == 2) {
            error.append(children.get(1).getLine()).append(" j\n");
        }
        return error.toString();
    }

    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();
        if (children.get(0).getType().equals(SyntaxType.Number)) {
            values.addAll(((Number) children.get(0)).calculate());
        } else if (children.get(0).getType().equals(SyntaxType.LVal)) {
            values.addAll(((LVal) children.get(0)).calculate());
        } else {
            values.addAll(((Exp) children.get(1)).calculate());
        }
        return values;
    }
}
