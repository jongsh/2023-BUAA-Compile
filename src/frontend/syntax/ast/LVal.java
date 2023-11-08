package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.type.ArrayType;
import frontend.semantics.llvmir.type.PointerType;
import frontend.semantics.llvmir.type.VarType;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.llvmir.value.instr.GepInstr;
import frontend.semantics.llvmir.value.instr.Instr;
import frontend.semantics.llvmir.value.instr.LoadInstr;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.Arrays;

public class LVal extends Node {
    private boolean isLeft;

    public LVal(ArrayList<Node> children) {
        super(SyntaxType.LVal, children);
        this.isLeft = false;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();

        String name = ((LeafNode) children.get(0)).getContent();
        VarSymbol varSymbol = SymbolManager.instance().getVarSymbol(name, true);
        int index = 0;
        if (children.size() > 1) {
            index = ((Exp) children.get(2)).calculate().get(0);
        }
        if (children.size() > 5) {
            index = index * varSymbol.getDimensions().get(1) + ((Exp) children.get(5)).calculate().get(0);
        }
        values.add(varSymbol.getInitial(index));
        return values;
    }

    // LVal → Ident {'[' Exp ']'} // c k
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();

        String identName = ((LeafNode) children.get(0)).getContent();
        if (SymbolManager.instance().getVarSymbol(identName, true) == null) {                // 判断变量名是否已经定义了
            error.append(children.get(0).getLine()).append(" c\n");
        } else {
            if (children.size() > 1) {
                int left = 0;
                int right = 0;
                for (int i = 1; i < children.size(); ++i) {
                    if (children.get(i).getType().equals(SyntaxType.RBRACK)) {
                        right++;
                    } else if (children.get(i).getType().equals(SyntaxType.LBRACK)) {
                        left++;
                    } else if (children.get(i).getType().equals(SyntaxType.Exp)) {
                        error.append(children.get(i).checkError());
                    }
                }
                if (left != right) {
                    error.append(children.get(0).getLine()).append(" k\n");
                }
            }
        }
        return error.toString();
    }

    // LVal → Ident {'[' Exp ']'} // c k
    @Override
    public Value genIR() {
        String identName = ((LeafNode) children.get(0)).getContent();
        VarSymbol varSymbol = SymbolManager.instance().getVarSymbol(identName, true);
        Value targetValue = varSymbol.getLLVMValue();

        ArrayList<Value> indexes = new ArrayList<>();
        if (((PointerType) targetValue.getValueType()).getTargetType() instanceof ArrayType) {
            indexes.add(IRBuilder.getInstance().newDigit(0));
        } else if (((PointerType) targetValue.getValueType()).getTargetType() instanceof PointerType) {
            targetValue = IRBuilder.getInstance().newLoadInstr(targetValue);
            IRBuilder.getInstance().addInstr((Instr) targetValue);
        } else if (!isLeft) {
            targetValue = IRBuilder.getInstance().newLoadInstr(targetValue);
            IRBuilder.getInstance().addInstr((Instr) targetValue);
        }

        for (int i = 1; i < children.size(); ++i) {
            if (children.get(i).getType().equals(SyntaxType.Exp)) {
                indexes.add(children.get(i).genIR());
            }
        }

        if (indexes.size() > 0) {
            targetValue = IRBuilder.getInstance().newGepInstr(targetValue, indexes);
            if (isLeft) {
                IRBuilder.getInstance().addInstr((Instr) targetValue);
            } else {
                if (((PointerType) targetValue.getValueType()).getTargetType() instanceof VarType) {
                    IRBuilder.getInstance().addInstr((Instr) targetValue);
                    targetValue = IRBuilder.getInstance().newLoadInstr(targetValue);
                    IRBuilder.getInstance().addInstr((Instr) targetValue);
                } else {
                    ((GepInstr) targetValue).addOperand(IRBuilder.getInstance().newDigit(0));
                    IRBuilder.getInstance().addInstr((Instr) targetValue);
                }
            }
        }
        return targetValue;
    }
}
