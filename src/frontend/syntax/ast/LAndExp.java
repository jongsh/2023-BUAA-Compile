package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LAndExp extends Node {
    public LAndExp(ArrayList<Node> children) {
        super(SyntaxType.LAndExp, children);
    }

    // LAndExp → EqExp | LAndExp '&&' EqExp
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }
}
