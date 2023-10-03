package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class CompUnit extends Node {

    public CompUnit(ArrayList<Node> children) {
        this.type = SyntaxType.CompUnit;
        this.children = children;
    }
}
