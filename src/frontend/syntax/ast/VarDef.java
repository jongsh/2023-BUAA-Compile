package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.type.PointerType;
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
            ArrayList<Integer> dimensions = new ArrayList<>();
            ArrayList<Integer> initials = null;
            int right = 0;
            for (int i = 1; i < children.size(); ++i) {
                if (children.get(i).getType().equals(SyntaxType.RBRACK)) {
                    right++;
                } else if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                    dimensions.add(((ConstExp) children.get(i)).calculate().get(0));
                }
            }
            if (right != dimensions.size()) {
                error.append(children.get(0).getLine()).append(" k\n");
            }
            if (children.get(children.size() - 1).getType().equals(SyntaxType.InitVal)) {
                error.append(children.get(children.size() - 1).checkError());
                if (SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.GLOBAL)) {
                    initials = ((InitVal) children.get(children.size() - 1)).calculate();
                }
            }
            SymbolManager.instance().addVarSymbol(false, identName, dimensions, initials);
        }
        return error.toString();
    }

    @Override
    public Value genIR() {
        // 维护符号表
        ArrayList<Integer> dimensions = new ArrayList<>();
        ArrayList<Integer> initials = null;
        String identName = ((LeafNode) children.get(0)).getContent();
        for (int i = 1; i < children.size(); ++i) {
            if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                dimensions.add(((ConstExp) children.get(i)).calculate().get(0));
            }
        }
        if (children.get(children.size() - 1).getType().equals(SyntaxType.InitVal) &&
                SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.GLOBAL)) {
            initials = ((InitVal) children.get(children.size() - 1)).calculate();
        }
        VarSymbol varSymbol = SymbolManager.instance().addVarSymbol(false, identName, dimensions, initials);

        // 生成中间代码
        if (SymbolManager.instance().getCurTableType().equals(SymbolTable.TableType.GLOBAL)) {
            GlobalVar globalVar = IRBuilder.getInstance().newGlobalVar(false, dimensions, initials);
            varSymbol.setLLVMValue(globalVar);
            IRBuilder.getInstance().addGlobalVar(globalVar);
        } else {
            AllocaInstr allocaInstr = IRBuilder.getInstance().newAllocaInstr(varSymbol.getDimensions());
            varSymbol.setLLVMValue(allocaInstr);
            IRBuilder.getInstance().addInstr(allocaInstr);
            if (children.get(children.size() - 1).getType().equals(SyntaxType.InitVal)) {
                ArrayList<Value> fromOperands = ((InitVal) children.get(children.size() - 1)).genIRs();
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
                            Arrays.asList(IRBuilder.getInstance().newDigit(0), null, null)
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
        }
        return null;
    }
}
