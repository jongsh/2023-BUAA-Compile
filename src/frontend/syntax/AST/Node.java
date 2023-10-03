package frontend.syntax.AST;

import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Node {
    protected SyntaxType type;

    protected ArrayList<Node> children;

    public SyntaxType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node child : children) {
            if (child != null) {
                sb.append(child.toString()).append("\n");
            }
        }
        sb.append("<").append(this.type).append(">");
        return sb.toString();
    }

    public Node searchNode(SyntaxType type) {
        Node res;
        for (Node child : children) {
            if (child != null) {
                if (child.getType().equals(type)) {
                    return child;
                }
                if ((res = child.searchNode(type)) != null) {
                    return res;
                }
            }
        }
        return null;
    }
}
