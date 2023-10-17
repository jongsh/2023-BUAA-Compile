package frontend.syntax.AST;

import frontend.semantics.Symbol.FuncSymbol;
import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class MainFuncDef extends Node {
    public MainFuncDef(ArrayList<Node> children) {
        super(SyntaxType.MainFuncDef, children);
    }

    // MainFuncDef → 'int' 'main' '(' ')' Block --- g j
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();

        FuncSymbol main = new FuncSymbol("main");
        main.setType("int");
        st.addSymbol(main);
        SymbolTable nextSt = new SymbolTable("main", st);
        st.addNext(nextSt);
        if (!children.get(3).getType().equals(SyntaxType.RPARENT)) {
            error.append(children.get(2).getLine()).append(" j\n");
        }
        error.append(children.get(children.size() - 1).checkError(nextSt));
        return error.toString();
    }
}
