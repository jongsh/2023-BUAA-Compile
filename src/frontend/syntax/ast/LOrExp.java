package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.llvmir.value.instr.BRInstr;
import frontend.semantics.symbol.SymbolTable;
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
        Value value;
        if (children.size() > 1) {
            BasicBlock block = IRBuilder.getInstance().newBasicBlock();
            IRBuilder.getInstance().setFalseBlock(block);  // 新的false block

            value = children.get(0).genIR();  // 递归解析 LAndExp
            BRInstr brInstr = IRBuilder.getInstance().newBRInstr(value, trueBlock, block);
            IRBuilder.getInstance().addInstr(brInstr);

            IRBuilder.getInstance().addBasicBlock(block);
        }
        value = children.get(children.size() - 1).genIR();   // 解析 EqExp
        return value;
    }
}
