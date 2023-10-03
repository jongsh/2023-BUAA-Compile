package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Exp extends Node {
    public Exp(ArrayList<Node> children) {
        this.type = SyntaxType.Exp;
        this.children = children;
    }
}
