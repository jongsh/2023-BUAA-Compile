package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class MainFuncDef extends Node {
    public MainFuncDef(ArrayList<Node> children) {
        this.type = SyntaxType.MainFuncDef;
        this.children = children;
    }
}
