package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Cond extends Node {
    public Cond(ArrayList<Node> children) {
        super(SyntaxType.Cond, children);
    }

    // Cond → LOrExp
    @Override
    public String checkError(SymbolTable st) {
        return children.get(0).checkError(st);
    }
}
