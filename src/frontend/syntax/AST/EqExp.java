package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class EqExp extends Node {
    public EqExp(ArrayList<Node> children) {
        this.type = SyntaxType.EqExp;
        this.children = children;
    }
}
