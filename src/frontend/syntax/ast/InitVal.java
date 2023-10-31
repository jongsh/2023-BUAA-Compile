package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class InitVal extends Node {
    public InitVal(ArrayList<Node> children) {
        super(SyntaxType.InitVal, children);
    }

    //  InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }
}
