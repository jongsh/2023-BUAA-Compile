package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class InitVal extends Node {
    public InitVal(ArrayList<Node> children) {
        super(SyntaxType.InitVal, children);
    }

    //  InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError(st));
        }
        return error.toString();
    }
}
