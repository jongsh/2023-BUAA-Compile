package frontend.syntax.AST;

import frontend.semantics.Symbol.FuncSymbol;
import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncType extends Node {
    public FuncType(ArrayList<Node> children) {
        super(SyntaxType.FuncType, children);
    }

    // FuncType → 'void' | 'int'
    @Override
    public String checkError(SymbolTable st) {
        FuncSymbol funcSymbol = (FuncSymbol) st.getSymbol(st.getName(),true);
        funcSymbol.setType(((LeafNode) children.get(0)).getContent());
        return "";
    }
}
