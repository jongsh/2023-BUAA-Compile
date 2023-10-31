package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Cond extends Node {
    public Cond(ArrayList<Node> children) {
        super(SyntaxType.Cond, children);
    }

    // Cond → LOrExp
    @Override
    public String checkError() {
        return children.get(0).checkError();
    }
}
