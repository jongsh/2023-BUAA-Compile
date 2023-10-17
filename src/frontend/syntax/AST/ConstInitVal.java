package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstInitVal extends Node {
    public ConstInitVal(ArrayList<Node> children) {
        super(SyntaxType.ConstInitVal, children);
    }

    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError(st));
        }
        return error.toString();
    }

    // ConstInitVal → ConstExp  | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
    public ArrayList<Integer> calculate(SymbolTable st) {
        ArrayList<Integer> values = new ArrayList<>();
        if (children.get(0).getType().equals(SyntaxType.LBRACE)) {
            for (int i = 1; i < children.size(); i += 2) {
                values.addAll(((ConstInitVal) children.get(i)).calculate(st));
            }
        } else {
            values.addAll(((ConstExp) children.get(0)).calculate(st));
        }
        return values;
    }
}
