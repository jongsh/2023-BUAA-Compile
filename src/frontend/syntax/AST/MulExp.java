package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class MulExp extends Node {
    public MulExp(ArrayList<Node> children) {
        this.type = SyntaxType.MulExp;
        this.children = children;
    }
}
