package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Block extends Node {
    public Block(ArrayList<Node> children) {
        super(SyntaxType.Block, children);
    }

    // Block → '{' { Decl | Stmt } '}'
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node node : children) {
            error.append(node.checkError());
        }
        if (SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.FUNC) ||
                SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.MAIN_FUNC)) {
            Node returnNode = children.get(children.size() - 2).searchNode(SyntaxType.RETURNTK);
            Node exp = children.get(children.size() - 2).searchNode(SyntaxType.Exp);
            Node ifNode = children.get(children.size() - 2).searchNode(SyntaxType.IFTK);
            Node forNode = children.get(children.size() - 2).searchNode(SyntaxType.FORTK);
            Node blockNode = children.get(children.size() - 2).searchNode(SyntaxType.Block);

            if (SymbolManager.instance().getCurTableFuncSymbol().getType().equals("void")) {
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
