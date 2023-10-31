package frontend.syntax.ast;

import frontend.semantics.symbol.FuncSymbol;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class MainFuncDef extends Node {
    public MainFuncDef(ArrayList<Node> children) {
        super(SyntaxType.MainFuncDef, children);
    }

    // MainFuncDef → 'int' 'main' '(' ')' Block --- g j
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        SymbolManager.instance().addFuncSymbol("main");
        SymbolManager.instance().createTable(SymbolTable.TableType.MAIN_FUNC, true, "main");
        SymbolManager.instance().setFuncType("int");

        if (!children.get(3).getType().equals(SyntaxType.RPARENT)) {
            error.append(children.get(2).getLine()).append(" j\n");
        }
        error.append(children.get(children.size() - 1).checkError());

        SymbolManager.instance().tracebackTable();
        return error.toString();
    }
}
