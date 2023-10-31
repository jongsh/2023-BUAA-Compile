package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class UnaryOp extends  Node {
    public UnaryOp(ArrayList<Node> children) {
        super(SyntaxType.UnaryOp, children);
    }

    @Override
    public String checkError() {
        return "";
    }

    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();
        values.add((children.get(0).getType().equals(SyntaxType.PLUS)) ? 1 : -1);
        return values;
    }
}
