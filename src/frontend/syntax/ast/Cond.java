package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.llvmir.value.instr.BRInstr;
import frontend.semantics.llvmir.value.instr.IcmpInstr;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Cond extends Node {
    public Cond(ArrayList<Node> children) {
        super(SyntaxType.Cond, children);
    }

    // Cond → LOrExp
    @Override
    public String checkError() {
        return children.get(0).checkError();
    }

    @Override
    public Value genIR() {
        BasicBlock trueBlock = IRBuilder.getInstance().getTrueBlock();
        BasicBlock falseBlock = (IRBuilder.getInstance().getFalseBlock() != null) ?
                IRBuilder.getInstance().getFalseBlock() : IRBuilder.getInstance().getLeaveBlock();

        Value value = children.get(0).genIR();

        BRInstr brInstr = IRBuilder.getInstance().newBRInstr(value, trueBlock, falseBlock);
        IRBuilder.getInstance().addInstr(brInstr);
        return null;
    }
}
