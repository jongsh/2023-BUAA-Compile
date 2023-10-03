package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Cond extends Node {
    public Cond(ArrayList<Node> children) {
        this.type = SyntaxType.Cond;
        this.children = children;
    }
}
