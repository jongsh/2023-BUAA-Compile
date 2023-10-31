package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncFParams extends Node {
    public FuncFParams(ArrayList<Node> children) {
        super(SyntaxType.FuncFParams, children);
    }

    // FuncFParams → FuncFParam { ',' FuncFParam }
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }
}
