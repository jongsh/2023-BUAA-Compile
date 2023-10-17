package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Number extends Node {
    public Number(ArrayList<Node> children) {
        super(SyntaxType.Number, children);
    }

    public ArrayList<Integer> calculate(SymbolTable st) {
        ArrayList<Integer> values = new ArrayList<>();
        values.add(Integer.parseInt(((LeafNode) children.get(0)).getContent()));
        return values;
    }
}
