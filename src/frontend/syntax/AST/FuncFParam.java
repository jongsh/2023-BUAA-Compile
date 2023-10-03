package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncFParam extends Node {

    public FuncFParam(ArrayList<Node> children) {
        this.type = SyntaxType.FuncFParam;
        this.children = children;
    }
}
