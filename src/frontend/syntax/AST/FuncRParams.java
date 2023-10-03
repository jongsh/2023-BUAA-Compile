package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncRParams extends Node {
    public FuncRParams(ArrayList<Node> children) {
        this.type = SyntaxType.FuncRParams;
        this.children = children;
    }
}
