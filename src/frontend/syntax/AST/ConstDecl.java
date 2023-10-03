package frontend.syntax.AST;

import frontend.lexer.LexType;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstDecl extends Node {

    public ConstDecl(ArrayList<Node> children) {
        this.type = SyntaxType.ConstDecl;
        this.children = children;
    }
}
