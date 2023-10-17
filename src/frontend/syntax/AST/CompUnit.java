package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class CompUnit extends Node {

    public CompUnit(ArrayList<Node> children) {
        super(SyntaxType.CompUnit, children);
    }

    // CompUnit → {Decl} {FuncDef} MainFuncDef
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError(st));
        }
        return error.toString();
    }
}
