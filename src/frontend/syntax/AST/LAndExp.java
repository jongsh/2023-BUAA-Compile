package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LAndExp extends Node {
    public LAndExp(ArrayList<Node> children) {
        super(SyntaxType.LAndExp, children);
    }

    // LAndExp → EqExp | LAndExp '&&' EqExp
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError(st));
        }
        return error.toString();
    }
}
