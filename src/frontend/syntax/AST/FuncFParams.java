package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncFParams extends Node {
    public FuncFParams(ArrayList<Node> children) {
        super(SyntaxType.FuncFParams, children);
    }

    // FuncFParams → FuncFParam { ',' FuncFParam }
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError(st));
        }
        return error.toString();
    }
}
