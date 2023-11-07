package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.llvmir.value.instr.StoreInstr;
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

    @Override
    public Value genIR() {
        Value leftValue = children.get(0).genIR();
        Value rightValue = children.get(2).genIR();
        StoreInstr storeInstr = IRBuilder.getInstance().newStoreInstr(rightValue, leftValue);
        IRBuilder.getInstance().addInstr(storeInstr);
        return null;
    }
}
