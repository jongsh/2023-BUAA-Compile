package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstInitVal extends Node {
    public ConstInitVal(ArrayList<Node> children) {
        super(SyntaxType.ConstInitVal, children);
    }

    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }

    // ConstInitVal → ConstExp  | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();
        if (children.get(0).getType().equals(SyntaxType.LBRACE)) {
            for (int i = 1; i < children.size(); i += 2) {
                if (children.get(i) instanceof ConstInitVal) {
                    values.addAll(((ConstInitVal) children.get(i)).calculate());
                }
            }
        } else {
            values.addAll(((ConstExp) children.get(0)).calculate());
        }
        return values;
    }
}
