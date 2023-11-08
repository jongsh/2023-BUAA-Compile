package frontend.syntax.ast;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstDecl extends Node {
    public ConstDecl(ArrayList<Node> children) {
        super(SyntaxType.ConstDecl, children);
    }

    // ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';' ---- i
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (int i = 2; i < children.size(); i += 2) {
            error.append((children.get(i)).checkError());
        }
        if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
            error.append(children.get(children.size() - 2).getLine()).append(" i\n");
        }
        return error.toString();
    }
}
