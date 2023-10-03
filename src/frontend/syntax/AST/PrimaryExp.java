package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class PrimaryExp extends Node {
    public PrimaryExp(ArrayList<Node> children) {
        this.type = SyntaxType.PrimaryExp;
        this.children = children;
    }
}
