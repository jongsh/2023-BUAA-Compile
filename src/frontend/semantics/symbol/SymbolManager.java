package frontend.semantics.symbol;

import frontend.syntax.ast.ConstInitVal;
import frontend.syntax.ast.FuncFParams;

import java.util.ArrayList;

public class SymbolManager {
    private final static SymbolManager instance = new SymbolManager();
    private SymbolTable curTable;
    private int depth;

    private SymbolManager() {
        this.curTable = null;
        this.depth = 0;
    }

    /**
     * 符号管理系统重置
     */
    public void reset() {
        curTable = null;
        depth = 0;
    }

    public static SymbolManager instance() {
        return instance;
    }

    /**
     * 创建并进入新的普通符号表
     */
    public void createTable(SymbolTable.TableType type, boolean permanent) {
        SymbolTable newTable = new SymbolTable(type, curTable);
        depth += 1;
        if (permanent && curTable != null) {
            curTable.addNext(newTable);
        }
        curTable = newTable;
    }

    /**
     * 创建并进入新的函数域符号表
     */
    public void createTable(SymbolTable.TableType type, boolean permanent, String funcName) {
        SymbolTable newTable = new SymbolTable(type, curTable, funcName);
        depth += 1;
        if (permanent) {
            curTable.addNext(newTable);
        }
        curTable = newTable;
    }

    /**
     * 返回到上一个符号表
     */
    public void tracebackTable() {
        curTable = curTable.getPrev();
        depth -= 1;
    }

    public VarSymbol getVarSymbol(String name, boolean isAll) {
        SymbolTable tempTable = curTable;
        int tempDepth = depth;
        while (isAll && tempDepth != 1 && tempTable.getSymbol(name) == null) {
            tempTable = tempTable.getPrev();
            tempDepth -= 1;
        }
        return (VarSymbol) tempTable.getSymbol(name);
    }

    public FuncSymbol getFuncSymbol(String name) {
        String actualName = name + "!";
        SymbolTable tempTable = curTable;
        for (int tempDepth = depth; tempDepth != 1; tempDepth--) {
            tempTable = tempTable.getPrev();
        }
        return (FuncSymbol) tempTable.getSymbol(actualName);
    }

    public VarSymbol addVarSymbol(boolean isConst, String name, ArrayList<Integer> dimensions, ArrayList<Integer> initials) {
        boolean isUndefined = depth == 1 && initials == null;
        VarSymbol newSymbol = new VarSymbol(isConst, name, dimensions, initials, isUndefined);
        curTable.addSymbol(newSymbol);
        return newSymbol;
    }

    public FuncSymbol addFuncSymbol(String name) {
        String actualName = name + "!";
        FuncSymbol newSymbol = new FuncSymbol(actualName);
        curTable.addSymbol(newSymbol);
        return newSymbol;
    }

    /**
     * 函数返回类型回填
     */
    public void setFuncType(String type) {
        String funcName = curTable.getFuncName();
        FuncSymbol funcSymbol = getFuncSymbol(funcName);
        funcSymbol.setType(type);
    }

    /**
     * 函数参数回填
     */
    public void addFuncParams(ArrayList<Integer> lens) {
        String funcName = curTable.getFuncName();
        FuncSymbol funcSymbol = getFuncSymbol(funcName);
        funcSymbol.addParam(lens);
    }

    public SymbolTable.TableType getCurTableType() {
        return curTable.getType();
    }

    public boolean isInTable(SymbolTable.TableType type) {
        SymbolTable tempTable = curTable;
        int tempDepth = depth;
        while (tempDepth > 0) {
            if (tempTable.getType().equals(type)) {
                return true;
            }
            tempTable = tempTable.getPrev();
            tempDepth -= 1;
        }
        return false;
    }

    public FuncSymbol getCurTableFuncSymbol() {
        return getFuncSymbol(curTable.getFuncName());
    }
}
