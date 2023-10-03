package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class AddExp extends Node {
    public AddExp(ArrayList<Node> children) {
        this.type = SyntaxType.AddExp;
        this.children = children;
    }
}
