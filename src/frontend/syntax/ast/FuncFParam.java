package frontend.syntax.ast;

import frontend.semantics.symbol.FuncSymbol;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncFParam extends Node {

    public FuncFParam(ArrayList<Node> children) {
        super(SyntaxType.FuncFParam, children);
    }

    // FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]  ----- b k
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        String identName = ((LeafNode) children.get(1)).getContent();
        if (SymbolManager.instance().getVarSymbol(identName, false) != null) {   // 判断参数名是否已经定义了
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
                    lens.add(((ConstExp) children.get(i)).calculate().get(0));
                }
            }
            if (right != lens.size()) {
                error.append(children.get(0).getLine()).append(" k\n");
            }
        }
        if (SymbolManager.instance().getVarSymbol(identName, false) == null) {                // 判断参数名是否已经定义了
            SymbolManager.instance().addVarSymbol(false, identName, lens, null);
        }
        SymbolManager.instance().addFuncParams(lens);

        return error.toString();
    }
}
