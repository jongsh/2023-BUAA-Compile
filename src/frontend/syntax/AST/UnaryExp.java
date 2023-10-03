package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class UnaryExp extends Node {
    public UnaryExp(ArrayList<Node> children) {
        this.type = SyntaxType.UnaryExp;
        this.children = children;
    }
}
