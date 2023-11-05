package frontend.semantics.llvmir;

import frontend.semantics.llvmir.type.ArrayType;
import frontend.semantics.llvmir.type.PointerType;
import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.type.VarType;
import frontend.semantics.llvmir.value.*;
import frontend.semantics.llvmir.value.instr.*;

import java.util.ArrayList;
import java.util.List;

public class IRBuilder {
    // 寄存器命名
    private static final String GLOBAL_VAR = "@global_";   // 全局变量
    private static final String GLOBAL_STR = "@string_";   // 全局字符串常量
    private static final String PARAM = "%param_";         // 函数参数
    private static final String BASIC_BLOCK = "block_";    // 基本块
    private static final String LOCAL_VAR = "%local_";     // 局部变量
    private int globalVarCnt = 0;
    private int globalStrCnt = 0;
    private int pramCnt = 0;
    private int basicBlockCnt = 0;
    private int localVarCnt = 0;
    private Module module;
    private Function curFunction;
    private BasicBlock curBasicBlock;
    private Instr curInstr;

    private static IRBuilder instance = new IRBuilder();

    public static IRBuilder getInstance() {
        return instance;
    }

    // --------------------- 模块 ------------------------------ //
    public void setModule() {
        this.module = new Module();
    }

    public Module getModule() {
        return this.module;
    }

    // --------------------- 全局变量 ------------------------------ //
    public GlobalVar newGlobalVar(boolean isConst, ArrayList<Integer> dimensions, ArrayList<Integer> initials) {
        String actualName = GLOBAL_VAR + (globalVarCnt++);
        GlobalVar globalVar;
        if (dimensions.size() == 0) {
            VarType type = new VarType(32, (initials != null) ? initials.get(0) : 0);
            globalVar = new GlobalVar(isConst, actualName, type, module);
        } else {
            ArrayType type = new ArrayType(dimensions, initials);
            globalVar = new GlobalVar(isConst, actualName, type, module);
        }
        return globalVar;
    }

    public void addGlobalVar(GlobalVar globalVar) {
        module.addGlobalVar(globalVar);
    }

    // ---------------------- 函数 ------------------------------ //
    public Function newFunction(String typeStr, String name) {
        String actualName = "@" + name;
        VarType type = (typeStr.equals("int")) ? new VarType(32) : new VarType(0);
        return new Function(actualName, type, module);
    }

    public void addFunction(Function function) {
        pramCnt = 0;
        basicBlockCnt = 0;
        module.addFunction(function);
        curFunction = function;
    }

    // ----------------------- 函数参数 ------------------------------ //
    public Param newParam(ArrayList<Integer> dimensions) {
        String actualName = PARAM + (pramCnt++);
        ValueType type;
        if (dimensions.size() == 0) {
            type = new VarType(32);
        } else if (dimensions.size() == 1) {
            type = new PointerType(new VarType(32));
        } else {
            type = PointerType.translate(new ArrayType(dimensions));
        }
        return new Param(actualName, type);
    }

    public void addParam(Param param) {
        curFunction.addParam(param);
    }

    // ----------------------- 基本块 ------------------------------ //
    public BasicBlock newBasicBlock() {
        String name = BASIC_BLOCK + (basicBlockCnt++);
        return new BasicBlock(name, curFunction);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
        localVarCnt = 0;
        curFunction.addBasicBlock(basicBlock);
        curBasicBlock = basicBlock;
    }

    // --------------------------- 常数 ----------------------------- //
    public Digit newDigit(Integer number) {
        return new Digit(number, new VarType(32));
    }

    // ------------------------- 指令 ---------------------------- //
    public AluInstr newAluInstr(String aluType) {
        InstrType instrType = (aluType.equals("+")) ? InstrType.ADD : (aluType.equals("-")) ? InstrType.SUB :
                (aluType.equals("*")) ? InstrType.MUL : (aluType.equals("/")) ? InstrType.DIV :
                        (aluType.equals("%")) ? InstrType.MOD : null;
        String name = LOCAL_VAR + (localVarCnt++);
        return new AluInstr(name, new VarType(32), instrType, curBasicBlock);
    }

    public CallInstr newCallInstr(String funcType) {
        if (funcType.equals("int")) {  // int 函数
            return new CallInstr(LOCAL_VAR + (localVarCnt++), new VarType(32), curBasicBlock);
        } else {                       // void 函数
            return new CallInstr("", new VarType(0), curBasicBlock);
        }
    }

    public AllocaInstr newAllocaInstr(ArrayList<Integer> dimensions) {
        ValueType type = (dimensions.size() == 0) ? new VarType(32) : new ArrayType(dimensions);
        return new AllocaInstr(LOCAL_VAR + (localVarCnt++), type, curBasicBlock);
    }

    public StoreInstr newStoreInstr(Value from, Value to) {
        StoreInstr storeInstr = new StoreInstr(curBasicBlock);
        storeInstr.addOperand(from);
        storeInstr.addOperand(new Value(to.getName(), new PointerType(to.getValueType())));
        return storeInstr;
    }

    public GepInstr newGepInstr(Value target, ArrayList<Value> indexes) {
        GepInstr gepInstr= new GepInstr(LOCAL_VAR + (localVarCnt++), target, curBasicBlock);
        for (Value index : indexes) {
            gepInstr.addOperand(index);
        }
        return gepInstr;
    }

    public void addInstr(Instr instr) {
        curBasicBlock.addInstr(instr);
        curInstr = instr;
    }
}
