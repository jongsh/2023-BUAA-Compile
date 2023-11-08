package frontend.syntax.ast;

import midend.llvmir.IRBuilder;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;
import midend.llvmir.value.instr.BRInstr;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LOrExp extends Node {
    public LOrExp(ArrayList<Node> children) {
        super(SyntaxType.LOrExp, children);
    }

    // LOrExp → LAndExp | LOrExp '||' LAndExp
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
        BasicBlock trueBlock = IRBuilder.getInstance().getTrueBlock();
        BasicBlock falseBlock = (IRBuilder.getInstance().getFalseBlock() != null) ?
                IRBuilder.getInstance().getFalseBlock() : IRBuilder.getInstance().getLeaveBlock();
        Value value;
        if (children.size() > 1) {
            BasicBlock block = IRBuilder.getInstance().newBasicBlock();
            IRBuilder.getInstance().setFalseBlock(block);  // 新的false block

            value = children.get(0).genIR();  // 递归解析 LAndExp
            BRInstr brInstr = IRBuilder.getInstance().newBRInstr(value, trueBlock, block);
            IRBuilder.getInstance().addInstr(brInstr);

            IRBuilder.getInstance().addBasicBlock(block);
        }
        IRBuilder.getInstance().setFalseBlock(falseBlock);
        value = children.get(children.size() - 1).genIR();
        return value;
    }
}
