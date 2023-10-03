package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class RelExp extends Node {
    public RelExp(ArrayList<Node> children) {
        this.type = SyntaxType.RelExp;
        this.children = children;
    }
}
