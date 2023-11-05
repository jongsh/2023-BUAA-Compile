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

public class FuncDef extends Node {
    public FuncDef(ArrayList<Node> children) {
        super(SyntaxType.FuncDef, children);
    }

    // FuncDef → FuncType Ident '(' [FuncFParams] ')' Block ---  b g j
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();

        String identName = ((LeafNode) children.get(1)).getContent();
        // 判断函数名是否已经定义了   全局函数名不能和全局变量同名
        if (SymbolManager.instance().getFuncSymbol(identName) != null ||
                SymbolManager.instance().getVarSymbol(identName, false) != null) {
            error.append(children.get(1).getLine()).append(" b\n");
            SymbolManager.instance().createTable(SymbolTable.TableType.FUNC, false, identName);
            SymbolManager.instance().addFuncSymbol(identName);
        } else {
            SymbolManager.instance().addFuncSymbol(identName);
            SymbolManager.instance().createTable(SymbolTable.TableType.FUNC, true, identName);
        }
        error.append(children.get(0).checkError());
        if (children.get(3).getType().equals(SyntaxType.FuncFParams)) {   // 检查是否有参数
            error.append(children.get(3).checkError());
            if (!children.get(4).getType().equals(SyntaxType.RPARENT)) {
                error.append(children.get(3).getLine()).append(" j\n");
            }
        } else {
            if (!children.get(3).getType().equals(SyntaxType.RPARENT)) {
                error.append(children.get(2).getLine()).append(" j\n");
            }
        }
        error.append(children.get(children.size() - 1).checkError());

        SymbolManager.instance().tracebackTable();
        return error.toString();
    }

    @Override
    public Value genIR() {
        // 维护符号表
        String identName = ((LeafNode) children.get(1)).getContent();
        FuncSymbol funcSymbol = SymbolManager.instance().addFuncSymbol(identName);
        SymbolManager.instance().createTable(SymbolTable.TableType.FUNC, true, identName);

        // 生成中间代码
        children.get(0).genIR();            // 返回值类型
        Function function = IRBuilder.getInstance().newFunction(funcSymbol.getType(), identName);
        IRBuilder.getInstance().addFunction(function);

        if (children.get(3).getType().equals(SyntaxType.FuncFParams)) {   // 检查是否有参数
            children.get(3).genIR();        // 函数形参
        }
        BasicBlock basicBlock = IRBuilder.getInstance().newBasicBlock();
        IRBuilder.getInstance().addBasicBlock(basicBlock);
        children.get(children.size() - 1).genIR();   // 函数块

        funcSymbol.setLLVMValue(function);
        SymbolManager.instance().tracebackTable();
        return null;
    }
}
