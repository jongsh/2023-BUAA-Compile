package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Number extends Node {
    public Number(ArrayList<Node> children) {
        this.type = SyntaxType.Number;
        this.children = children;
    }
}
