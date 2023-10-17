package frontend.syntax.AST;

import frontend.semantics.Symbol.FuncSymbol;
import frontend.semantics.Symbol.SymbolTable;
import frontend.semantics.Symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncFParam extends Node {

    public FuncFParam(ArrayList<Node> children) {
        super(SyntaxType.FuncFParam, children);
    }

    // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]  ----- b k
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        String identName = ((LeafNode) children.get(1)).getContent();
        if (st.getSymbol(identName, false) != null) {                // 判断参数名是否已经定义了
            error.append(children.get(1).getLine()).append(" b\n");
        }
        ArrayList<Integer> lens = new ArrayList<>();  // 函数参数维度
        if (children.size() > 2) {
            lens.add(0);
            int right = 0;
            for (int i = 2; i < children.size(); i++) {
                if (children.get(i).getType().equals(SyntaxType.RBRACK)) {
                    right++;
                } else if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                    lens.add(((ConstExp) children.get(i)).calculate(st).get(0));
                }
            }
            if (right != lens.size()) {
                error.append(children.get(0).getLine()).append(" k\n");
            }
        }
        if (st.getSymbol(identName, false) == null) {                // 判断参数名是否已经定义了
            st.addSymbol(new VarSymbol(identName, lens));
        }
        ((FuncSymbol) st.getSymbol(st.getName(), true)).addParam(lens);

        return error.toString();
    }
}
