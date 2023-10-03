package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LOrExp extends Node {
    public LOrExp(ArrayList<Node> children) {
        this.type = SyntaxType.LOrExp;
        this.children = children;
    }
}
