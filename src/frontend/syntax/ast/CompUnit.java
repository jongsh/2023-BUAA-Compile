package frontend.syntax.ast;

import frontend.semantics.llvmir.Value;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class CompUnit extends Node {

    public CompUnit(ArrayList<Node> children) {
        super(SyntaxType.CompUnit, children);
    }

    // CompUnit → {Decl} {FuncDef} MainFuncDef
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }
}
