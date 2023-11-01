package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.Param;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.symbol.SymbolManager;
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
        SymbolManager.instance().addFuncParam(lens);
        return error.toString();
    }

    @Override
    public Value genIR() {
        // 维护符号表
        String identName = ((LeafNode) children.get(1)).getContent();
        ArrayList<Integer> dimensions = new ArrayList<>();  // 函数参数维度
        if (children.size() > 2) {
            dimensions.add(0);
            for (int i = 2; i < children.size(); i++) {
                if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                    dimensions.add(((ConstExp) children.get(i)).calculate().get(0));
                }
            }
        }
        SymbolManager.instance().addVarSymbol(false, identName, dimensions, null);
        SymbolManager.instance().addFuncParam(dimensions);

        //
        Param param = IRBuilder.getInstance().newParam(dimensions);
        IRBuilder.getInstance().addParam(param);
        return null;
    }
}
