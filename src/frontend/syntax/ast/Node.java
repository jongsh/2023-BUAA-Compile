package frontend.syntax.ast;

import midend.llvmir.IRBuilder;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;
import frontend.syntax.SyntaxType;
import midend.llvmir.value.instr.BrInstr;

import java.util.ArrayList;

public class Node {
    protected int line;
    protected SyntaxType type;
    protected ArrayList<Node> children;

    public Node(SyntaxType type, int line) {
        this.type = type;
        this.children = new ArrayList<>();
        this.line = line;
    }

    public Node(SyntaxType type, ArrayList<Node> children) {
        this.type = type;
        this.children = children;
        this.line = children.get(children.size() - 1).getLine();
    }

    public SyntaxType getType() {
        return this.type;
    }

    public int getLine() {
        return line;
    }

    public int size() {
        return children.size();
    }

    public Node searchNode(SyntaxType type) {
        Node res;
        if (children.size() > 0) {
            for (Node child : children) {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node child : children) {
            if (child != null) {
                sb.append(child).append("\n");
            }
        }
        sb.append("<").append(this.type).append(">");
        return sb.toString();
    }

    public String checkError() {
        return "";
    }

    public Value genIR() {
        for (Node child : children) {
            child.genIR();
        }
        return null;
    }
}
