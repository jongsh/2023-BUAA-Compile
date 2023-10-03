package frontend.syntax.AST;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class VarDecl extends Node {

    public VarDecl(ArrayList<Node> children) {
        this.type = SyntaxType.VarDecl;
        this.children = children;
    }
}
