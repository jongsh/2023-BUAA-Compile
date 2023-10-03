package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncDef extends Node {
    public FuncDef(ArrayList<Node> children) {
        this.type = SyntaxType.FuncDef;
        this.children = children;
    }
}
