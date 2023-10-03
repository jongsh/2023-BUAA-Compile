package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LVal extends Node {
    public LVal(ArrayList<Node> children) {
        this.type = SyntaxType.LVal;
        this.children = children;
    }
}
