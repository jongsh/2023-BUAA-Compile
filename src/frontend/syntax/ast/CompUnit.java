package frontend.syntax.ast;

import midend.llvmir.IRBuilder;
import midend.llvmir.value.Value;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class CompUnit extends Node {

    public CompUnit(ArrayList<Node> children) {
        super(SyntaxType.CompUnit, children);
    }

    // CompUnit → {Decl} {FuncDef} MainFuncDef
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        SymbolManager.instance().createTable(SymbolTable.TableType.GLOBAL, true);
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }

    @Override
    public Value genIR() {
        IRBuilder.getInstance().setModule();
        SymbolManager.instance().createTable(SymbolTable.TableType.GLOBAL, true);
        return super.genIR();
    }
}
