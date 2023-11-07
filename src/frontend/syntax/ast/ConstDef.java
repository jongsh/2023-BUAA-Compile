package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.Digit;
import frontend.semantics.llvmir.value.GlobalVar;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.llvmir.value.instr.AllocaInstr;
import frontend.semantics.llvmir.value.instr.GepInstr;
import frontend.semantics.llvmir.value.instr.StoreInstr;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.Arrays;

public class ConstDef extends Node {
    public ConstDef(ArrayList<Node> children) {
        super(SyntaxType.ConstDef, children);
    }


    // ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal ---- b k
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();

        String identName = ((LeafNode) children.get(0)).getContent();
        if (SymbolManager.instance().getVarSymbol(identName, false) != null) {
            error.append(children.get(0).getLine()).append(" b\n");
        } else {
            ArrayList<Integer> dimension = new ArrayList<>();
            int right = 0;
            for (int i = 1; i < children.size(); ++i) {
                if (children.get(i).getType().equals(SyntaxType.RBRACK)) {
                    right++;
                }
                if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                    dimension.add(((ConstExp) children.get(i)).calculate().get(0));
                }
            }
            if (right != dimension.size()) {
                error.append(children.get(0).getLine()).append(" k\n");
            }
            SymbolManager.instance().addVarSymbol(true, identName, dimension,
                    ((ConstInitVal) children.get(children.size() - 1)).calculate()
            );
        }

        return error.toString();
    }

    @Override
    public Value genIR() {
        // 维护符号表
        String identName = ((LeafNode) children.get(0)).getContent();
        ArrayList<Integer> dimensions = new ArrayList<>();
        for (int i = 1; i < children.size(); ++i) {
            if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                dimensions.add(((ConstExp) children.get(i)).calculate().get(0));
            }
        }
        VarSymbol varSymbol = SymbolManager.instance().addVarSymbol(true, identName, dimensions,
                ((ConstInitVal) children.get(children.size() - 1)).calculate());

        // 生成中间代码
        if (SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.GLOBAL)) {
            GlobalVar globalVar = IRBuilder.getInstance().newGlobalVar(
                    true, dimensions, varSymbol.getInitials()
            );
            varSymbol.setLLVMValue(globalVar);
            IRBuilder.getInstance().addGlobalVar(globalVar);

        } else {
            AllocaInstr allocaInstr = IRBuilder.getInstance().newAllocaInstr(varSymbol.getDimensions());
            varSymbol.setLLVMValue(allocaInstr);
            IRBuilder.getInstance().addInstr(allocaInstr);
            ArrayList<Value> fromOperands = ((ConstInitVal) children.get(children.size() - 1)).genIRs();
            if (dimensions.size() == 0) {  // 普通变量
                StoreInstr storeInstr = IRBuilder.getInstance().newStoreInstr(fromOperands.get(0), allocaInstr);
                IRBuilder.getInstance().addInstr(storeInstr);
            } else if (dimensions.size() == 1) {  // 一维数组
                ArrayList<Value> indexes = new ArrayList<>(
                        Arrays.asList(IRBuilder.getInstance().newDigit(0), null)
                );
                for (int num = 0; num < dimensions.get(0); ++num) {
                    indexes.set(1, IRBuilder.getInstance().newDigit(num));
                    GepInstr gepInstr = IRBuilder.getInstance().newGepInstr(allocaInstr, indexes);
                    StoreInstr storeInstr = IRBuilder.getInstance().newStoreInstr(fromOperands.get(num), gepInstr);
                    IRBuilder.getInstance().addInstr(gepInstr);
                    IRBuilder.getInstance().addInstr(storeInstr);
                }
            } else if (dimensions.size() == 2) {
                ArrayList<Value> indexes = new ArrayList<>(
                        Arrays.asList(IRBuilder.getInstance().newDigit(0), null)
                );
                for (int num1 = 0; num1 < dimensions.get(0); ++num1) {
                    indexes.set(1, IRBuilder.getInstance().newDigit(num1));
                    for (int num2 = 0; num2 < dimensions.get(1); ++num2) {
                        indexes.set(2, IRBuilder.getInstance().newDigit(num2));
                        GepInstr gepInstr = IRBuilder.getInstance().newGepInstr(allocaInstr, indexes);
                        StoreInstr storeInstr = IRBuilder.getInstance().newStoreInstr(
                                fromOperands.get(num2 + num1 * dimensions.get(1)), gepInstr
                        );
                        IRBuilder.getInstance().addInstr(gepInstr);
                        IRBuilder.getInstance().addInstr(storeInstr);
                    }
                }
            }
        }
        return null;
    }
}
