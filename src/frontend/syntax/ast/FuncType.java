package frontend.syntax.ast;

import midend.llvmir.value.Value;
import frontend.semantics.symbol.SymbolManager;
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

    @Override
    public Value genIR() {
        SymbolManager.instance().setFuncType(((LeafNode) children.get(0)).getContent());
        return null;
    }
}
