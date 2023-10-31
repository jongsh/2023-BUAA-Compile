package frontend.syntax.ast;

import frontend.semantics.symbol.FuncSymbol;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncType extends Node {
    public FuncType(ArrayList<Node> children) {
        super(SyntaxType.FuncType, children);
    }

    // FuncType → 'void' | 'int'
    @Override
    public String checkError() {
        SymbolManager.instance().setFuncType(((LeafNode) children.get(0)).getContent());
        return "";
    }
}
