package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Stmt extends Node {
    public Stmt(ArrayList<Node> children) {
        this.type = SyntaxType.Stmt;
        this.children = children;
    }
}
