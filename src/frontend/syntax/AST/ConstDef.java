package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstDef extends Node {
    public ConstDef(ArrayList<Node> children) {
        this.type = SyntaxType.ConstDef;
        this.children = children;
    }
}
