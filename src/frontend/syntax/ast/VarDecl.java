package frontend.syntax.ast;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class VarDecl extends Node {

    public VarDecl(ArrayList<Node> children) {
        super(SyntaxType.VarDecl, children);
    }

    // VarDecl → BType VarDef { ',' VarDef } ';' // i
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (int i = 1; i < children.size(); i += 2) {
            error.append((children.get(i)).checkError());
        }
        if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
            error.append(children.get(children.size() - 1).getLine()).append(" i\n");
        }
        return error.toString();
    }

}
