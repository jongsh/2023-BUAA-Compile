package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class EqExp extends Node {
    public EqExp(ArrayList<Node> children) {
        super(SyntaxType.EqExp, children);
    }

    // EqExp → RelExp | EqExp ('==' | '!=') RelExp
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }
}
