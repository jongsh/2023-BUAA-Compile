package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncFParams extends Node {
    public FuncFParams(ArrayList<Node> children) {
        this.type = SyntaxType.FuncFParams;
        this.children = children;
    }

}
