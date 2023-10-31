package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ForStmt extends Node {
    public ForStmt(ArrayList<Node> children) {
        super(SyntaxType.ForStmt, children);
    }

    // ForStmt → LVal '=' Exp  ----- h
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        LeafNode ident = ((LeafNode) children.get(0).searchNode(SyntaxType.IDENFR));
        VarSymbol symbol = SymbolManager.instance().getVarSymbol(ident.getContent(), true);

        if (symbol != null && symbol.isConst()) {
            error.append(children.get(0).getLine()).append(" h\n");
        }
        return error.toString();
    }
}
