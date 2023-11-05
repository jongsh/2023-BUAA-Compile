package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.BasicBlock;
import frontend.semantics.llvmir.value.Function;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.symbol.FuncSymbol;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class MainFuncDef extends Node {
    public MainFuncDef(ArrayList<Node> children) {
        super(SyntaxType.MainFuncDef, children);
    }

    // MainFuncDef → 'int' 'main' '(' ')' Block --- g j
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        SymbolManager.instance().addFuncSymbol("main");
        SymbolManager.instance().createTable(SymbolTable.TableType.MAIN_FUNC, true, "main");
        SymbolManager.instance().setFuncType("int");

        if (!children.get(3).getType().equals(SyntaxType.RPARENT)) {
            error.append(children.get(2).getLine()).append(" j\n");
        }
        error.append(children.get(children.size() - 1).checkError());

        SymbolManager.instance().tracebackTable();
        return error.toString();
    }

    @Override
    public Value genIR() {
        // 维护符号表
        FuncSymbol funcSymbol = SymbolManager.instance().addFuncSymbol("main");
        SymbolManager.instance().createTable(SymbolTable.TableType.MAIN_FUNC, true, "main");
        SymbolManager.instance().setFuncType("int");

        // 生成中间代码
        Function function = IRBuilder.getInstance().newFunction(funcSymbol.getType(), "main");
        IRBuilder.getInstance().addFunction(function);

        BasicBlock basicBlock = IRBuilder.getInstance().newBasicBlock();
        IRBuilder.getInstance().addBasicBlock(basicBlock);
        children.get(children.size() - 1).genIR();   // 函数块

        funcSymbol.setLLVMValue(function);
        SymbolManager.instance().tracebackTable();
        return null;
    }
}
