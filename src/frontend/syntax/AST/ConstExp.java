package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstExp extends Node {
    public ConstExp(ArrayList<Node> children) {
        this.type = SyntaxType.ConstExp;
        this.children = children;
    }
}
