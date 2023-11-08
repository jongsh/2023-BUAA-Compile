package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.type.VarType;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.llvmir.value.instr.BRInstr;
import frontend.semantics.llvmir.value.instr.Instr;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LAndExp extends Node {
    public LAndExp(ArrayList<Node> children) {
        super(SyntaxType.LAndExp, children);
    }

    // LAndExp → EqExp | LAndExp '&&' EqExp
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }

    @Override
    public Value genIR() {
        BasicBlock falseBlock = (IRBuilder.getInstance().getFalseBlock() != null) ?
                IRBuilder.getInstance().getFalseBlock() : IRBuilder.getInstance().getLeaveBlock();
        BasicBlock trueBlock = IRBuilder.getInstance().getTrueBlock();

        Value value;
        if (children.size() > 1) {
            BasicBlock block = IRBuilder.getInstance().newBasicBlock();
            IRBuilder.getInstance().setTrueBlock(block);  // 新的 true block

            value = children.get(0).genIR();  // 递归解析 LAndExp
            if (((VarType) value.getValueType()).getWidth() != 1) {
                value = IRBuilder.getInstance().newIcmpInstr(
                        "!=", value, IRBuilder.getInstance().newDigit(0));
                IRBuilder.getInstance().addInstr((Instr) value);
            }

            BRInstr brInstr = IRBuilder.getInstance().newBRInstr(value, block, falseBlock);
            IRBuilder.getInstance().addInstr(brInstr);

            IRBuilder.getInstance().addBasicBlock(block);
        }
        IRBuilder.getInstance().setTrueBlock(trueBlock);  // 新的 true block
        value = children.get(children.size() - 1).genIR();   // 解析 EqExp
        if (((VarType) value.getValueType()).getWidth() != 1) {
            value = IRBuilder.getInstance().newIcmpInstr(
                    "!=", value, IRBuilder.getInstance().newDigit(0));
            IRBuilder.getInstance().addInstr((Instr) value);
        }
        return value;
    }
}
