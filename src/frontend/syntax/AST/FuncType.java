package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncType extends Node {
    public FuncType(ArrayList<Node> children) {
        this.type = SyntaxType.FuncType;
        this.children = children;
    }
}
