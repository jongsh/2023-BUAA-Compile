package frontend.syntax.AST;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstInitVal extends Node {
    public ConstInitVal(ArrayList<Node> children) {
        this.type = SyntaxType.ConstInitVal;
        this.children = children;
    }

}
