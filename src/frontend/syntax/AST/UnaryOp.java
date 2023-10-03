package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class UnaryOp extends  Node {
    public UnaryOp(ArrayList<Node> children) {
        this.type = SyntaxType.UnaryOp;
        this.children = children;
    }
}
