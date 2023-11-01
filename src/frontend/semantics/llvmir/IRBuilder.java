package frontend.semantics.llvmir;

import frontend.semantics.llvmir.type.ArrayType;
import frontend.semantics.llvmir.type.VarType;
import frontend.semantics.llvmir.value.*;

import java.util.ArrayList;

public class IRBuilder {
    // 寄存器命名
    private final static String GLOBAL_VAR = "@global_";   // 全局变量
    private final static String GLOBAL_STR = "@string_";   // 全局字符串常量
    private final static String FUNCTION = "@func_";       // 函数
    private final static String PARAM = "%param_";         // 函数参数
    private final static String BASIC_BLOCK = "block_";    // 基本块
    private final static String LOCAL_VAR = "%local_";     // 局部变量
    private int globalVarCnt = 0;
    private int globalStrCnt = 0;
    private int pramCnt = 0;
    private int basicBlockCnt = 0;
    private int localVarCnt = 0;

    private Module module;
    private Function curFunction;
    private BasicBlock basicBlock;

    private final static IRBuilder instance = new IRBuilder();

    public static IRBuilder getInstance() {
        return instance;
    }

    public void setModule() {
        this.module = new Module();
    }

    public Module getModule() {
        return this.module;
    }

    public GlobalVar newGlobalVar(boolean isConst, ArrayList<Integer> dimensions,
                                  ArrayList<Integer> initials) {
        String actualName = GLOBAL_VAR + (globalVarCnt++);
        GlobalVar globalVar;
        if (dimensions.size() == 0) {
            VarType type = new VarType(32, (initials != null) ? initials.get(0) : 0);
            globalVar = new GlobalVar(isConst, actualName, type);
        } else {
            ArrayType type = new ArrayType(dimensions, initials);
            globalVar = new GlobalVar(isConst, actualName, type);
        }
        return globalVar;
    }

    public void addGlobalVar(GlobalVar globalVar) {
        module.addGlobalVar(globalVar);
    }
}
