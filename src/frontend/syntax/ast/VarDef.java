package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.GlobalVar;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class VarDef extends Node {
    public VarDef(ArrayList<Node> children) {
        super(SyntaxType.VarDef, children);
    }

    // VarDef → Ident { '[' ConstExp ']' }            -------- b
    //    | Ident { '[' ConstExp ']' } '=' InitVal    -------- k
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        String identName = ((LeafNode) children.get(0)).getContent();
        if (SymbolManager.instance().getVarSymbol(identName, false) != null) {
            error.append(children.get(0).getLine()).append(" b\n");
            error.append(children.get(children.size() - 1).checkError());
        } else {
            ArrayList<Integer> dimension = new ArrayList<>();
            ArrayList<Integer> initials = null;
            int right = 0;
            for (int i = 1; i < children.size(); ++i) {
                if (children.get(i).getType().equals(SyntaxType.RBRACK)) {
                    right++;
                } else if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                    dimension.add(((ConstExp) children.get(i)).calculate().get(0));
                }
            }
            if (right != dimension.size()) {
                error.append(children.get(0).getLine()).append(" k\n");
            }
            if (children.get(children.size() - 1).getType().equals(SyntaxType.InitVal)) {
                error.append(children.get(children.size() - 1).checkError());
                if (SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.GLOBAL)) {
                    initials = ((InitVal) children.get(children.size() - 1)).calculate();
                }
            }
            SymbolManager.instance().addVarSymbol(false, identName, dimension, initials);
        }
        return error.toString();
    }

    @Override
    public Value genIR() {
        // 维护符号表
        ArrayList<Integer> dimension = new ArrayList<>();
        ArrayList<Integer> initials = null;
        String identName = ((LeafNode) children.get(0)).getContent();
        for (int i = 1; i < children.size(); ++i) {
            if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                dimension.add(((ConstExp) children.get(i)).calculate().get(0));
            }
        }
        if (children.get(children.size() - 1).getType().equals(SyntaxType.InitVal) &&
                SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.GLOBAL)) {
            initials = ((InitVal) children.get(children.size() - 1)).calculate();
        }
        VarSymbol varSymbol = SymbolManager.instance().addVarSymbol(false, identName, dimension, initials);

        // 生成中间代码
        if (SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.GLOBAL)) {
            GlobalVar globalVar = IRBuilder.getInstance().newGlobalVar(false, dimension, initials);
            varSymbol.setLLVMValue(globalVar);
            IRBuilder.getInstance().addGlobalVar(globalVar);
        } else {
            /* TODO 局部定义*/
        }

        return null;
    }
}
