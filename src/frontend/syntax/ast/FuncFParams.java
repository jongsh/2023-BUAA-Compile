package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;
import midend.llvmir.IRBuilder;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;
import midend.llvmir.value.instr.BrInstr;

import java.util.ArrayList;

public class FuncFParams extends Node {
    public FuncFParams(ArrayList<Node> children) {
        super(SyntaxType.FuncFParams, children);
    }

    // FuncFParams → FuncFParam { ',' FuncFParam }
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
        for (Node child : children) {
            child.genIR();
        }
        BasicBlock basicBlock = IRBuilder.getInstance().newBasicBlock();
        BrInstr brInstr = IRBuilder.getInstance().newBRInstr(basicBlock);
        IRBuilder.getInstance().addInstr(brInstr);
        IRBuilder.getInstance().addBasicBlock(basicBlock);
        return null;
    }
}
