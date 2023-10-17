package frontend.syntax.AST;

import frontend.semantics.Symbol.FuncSymbol;
import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncDef extends Node {
    public FuncDef(ArrayList<Node> children) {
        super(SyntaxType.FuncDef, children);
    }

    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block ---  b g j
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        String identNameTemp = ((LeafNode) children.get(1)).getContent();
        String identName = identNameTemp + "!";
        SymbolTable nextSt = new SymbolTable(identName, st);
        if (st.getSymbol(identNameTemp, false) != null ||st.getSymbol(identName, false) != null) {                // 判断函数名是否已经定义了
            error.append(children.get(1).getLine()).append(" b\n");
            nextSt.addSymbol(new FuncSymbol(identName));
        } else {
            st.addSymbol(new FuncSymbol(identName));
            st.addNext(nextSt);
        }
        error.append(children.get(0).checkError(nextSt));
        if (children.get(3).getType().equals(SyntaxType.FuncFParams)) {   // 检查是否有参数
            error.append(children.get(3).checkError(nextSt));
            if (!children.get(4).getType().equals(SyntaxType.RPARENT)) {
                error.append(children.get(3).getLine()).append(" j\n");
            }
        } else {
            if (!children.get(3).getType().equals(SyntaxType.RPARENT)) {
                error.append(children.get(2).getLine()).append(" j\n");
            }
        }
        error.append(children.get(children.size() - 1).checkError(nextSt));


        return error.toString();
    }
}
