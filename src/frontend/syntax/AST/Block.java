package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Block extends Node {
    public Block(ArrayList<Node> children) {
        this.type = SyntaxType.Block;
        this.children = children;
    }
}
