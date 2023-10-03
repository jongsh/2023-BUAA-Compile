package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class InitVal extends Node {
    public InitVal(ArrayList<Node> children) {
        this.type = SyntaxType.InitVal;
        this.children = children;
    }
}
