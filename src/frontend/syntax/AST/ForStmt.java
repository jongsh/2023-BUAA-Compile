package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ForStmt extends Node {
    public ForStmt(ArrayList<Node> children) {
        this.type = SyntaxType.ForStmt;
        this.children = children;
    }
}
