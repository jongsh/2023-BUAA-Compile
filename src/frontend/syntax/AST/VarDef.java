package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class VarDef extends Node {
    public VarDef(ArrayList<Node> children) {
        this.type = SyntaxType.VarDef;
        this.children = children;
    }
}
