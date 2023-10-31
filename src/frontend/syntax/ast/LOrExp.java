package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LOrExp extends Node {
    public LOrExp(ArrayList<Node> children) {
        super(SyntaxType.LOrExp, children);
    }

    // LOrExp → LAndExp | LOrExp '||' LAndExp
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }
}
