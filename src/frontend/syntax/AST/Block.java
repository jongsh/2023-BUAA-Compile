package frontend.syntax.AST;

import frontend.semantics.Symbol.FuncSymbol;
import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Block extends Node {
    public Block(ArrayList<Node> children) {
        super(SyntaxType.Block, children);
    }

    // Block → '{' { Decl | Stmt } '}'
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        for (Node node : children) {
            error.append(node.checkError(st));
        }
        if (!st.getName().equals("") && !st.getName().equals("if") && !st.getName().equals("for")) {
            Node returnNode = children.get(children.size() - 2).searchNode(SyntaxType.RETURNTK);
            Node exp = children.get(children.size() - 2).searchNode(SyntaxType.Exp);
            Node ifNode = children.get(children.size() - 2).searchNode(SyntaxType.IFTK);
            Node forNode = children.get(children.size() - 2).searchNode(SyntaxType.FORTK);
            Node blockNode = children.get(children.size() - 2).searchNode(SyntaxType.Block);
            if (((FuncSymbol) st.getSymbol(st.getName(), true)).getType().equals("void")) {
                if (returnNode != null && exp != null && ifNode == null && forNode == null && blockNode == null) {
                    error.append(returnNode.getLine()).append(" f\n");
                }
            } else {
                if (ifNode != null || forNode != null || blockNode != null || returnNode == null) {
                    error.append(children.get(children.size() - 1).getLine()).append(" g\n");
                }
            }
        }
        return error.toString();
    }
}
