package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LAndExp extends Node {
    public LAndExp(ArrayList<Node> children) {
        this.type = SyntaxType.LAndExp;
        this.children = children;
    }
}
