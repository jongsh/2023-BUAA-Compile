package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class RelExp extends Node {
    public RelExp(ArrayList<Node> children) {
        super(SyntaxType.RelExp, children);
    }

    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }
}
