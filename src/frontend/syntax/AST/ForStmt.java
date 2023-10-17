package frontend.syntax.AST;

import frontend.semantics.Symbol.Symbol;
import frontend.semantics.Symbol.SymbolTable;
import frontend.semantics.Symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ForStmt extends Node {
    public ForStmt(ArrayList<Node> children) {
        super(SyntaxType.ForStmt, children);
    }

    // ForStmt → LVal '=' Exp  ----- h
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError(st));
        }
        LeafNode ident = ((LeafNode) children.get(0).searchNode(SyntaxType.IDENFR));
        Symbol symbol = st.getSymbol(ident.getContent(), true);

        if (symbol != null && ((VarSymbol) symbol).isConst()) {
            error.append(children.get(0).getLine()).append(" h\n");
        }
        return error.toString();
    }
}
