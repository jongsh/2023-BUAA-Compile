package midend.llvmir;

import midend.llvmir.type.ArrayType;
import midend.llvmir.type.PointerType;
import midend.llvmir.type.ValueType;
import midend.llvmir.type.VarType;
import midend.llvmir.value.*;
import midend.llvmir.value.Module;
import midend.llvmir.value.instr.*;
import util.CalTool;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private Module module = null;
    private Function curFunction = null;
    private BasicBlock curBasicBlock = null;
    private final ArrayList<BasicBlock> context = new ArrayList<>();  // 流程控制块的上下文信息
    private int contextPos = 0;
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

    // ----------------------- 全局字符串 ------------------------ //
    public GlobalStr newGlobalStr(String str) {
        int length = CalTool.getLLVMStrLength(str) + 1;
        return new GlobalStr(GLOBAL_STR + (globalStrCnt++), str, length, module);
    }

    public void addGlobalStr(GlobalStr globalStr) {
        module.addGlobalStr(globalStr);
    }

    // ---------------------- 函数 ------------------------------ //
    public Function newFunction(String typeStr, String name) {
        String actualName = "@" + name;
        VarType type = (typeStr.equals("int")) ? new VarType(32) : new VarType(0);
        return new Function(actualName, type, module);
    }

    public void addFunction(Function function) {
        pramCnt = 0;
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

    public AllocaInstr addParam(Param param) {
        curFunction.addParam(param);
        AllocaInstr allocaInstr = new AllocaInstr(LOCAL_VAR + (localVarCnt++), param.getValueType(), curBasicBlock);
        StoreInstr storeInstr = new StoreInstr(curBasicBlock, param, allocaInstr);
        addInstr(allocaInstr);
        addInstr(storeInstr);
        return allocaInstr;
    }

    // ----------------------- 基本块 ------------------------------ //
    public BasicBlock newBasicBlock() {
        String name = BASIC_BLOCK + (basicBlockCnt++);
        return new BasicBlock(name, curFunction);
    }

    public void addBasicBlock(BasicBlock basicBlock) {
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

    public CallInstr newCallInstr(Function function) {
        if (((VarType) function.getValueType()).getWidth() == 0) {
            return new CallInstr("", function, curBasicBlock);
        } else {
            return new CallInstr(LOCAL_VAR + (localVarCnt++), function, curBasicBlock);
        }
    }

    public AllocaInstr newAllocaInstr(ArrayList<Integer> dimensions) {
        ValueType type = (dimensions.size() == 0) ? new VarType(32) : new ArrayType(dimensions);
        return new AllocaInstr(LOCAL_VAR + (localVarCnt++), type, curBasicBlock);
    }

    public StoreInstr newStoreInstr(Value from, Value to) {
        return new StoreInstr(curBasicBlock, from, to);
    }

    public LoadInstr newLoadInstr(Value target) {
        return new LoadInstr(LOCAL_VAR + (localVarCnt++), target, curBasicBlock);
    }

    public GepInstr newGepInstr(Value target, List<Value> indexes) {
        GepInstr gepInstr = new GepInstr(LOCAL_VAR + (localVarCnt++), target, curBasicBlock);
        for (Value index : indexes) {
            if (index != null) {
                gepInstr.addOperand(index);
            }
        }
        return gepInstr;
    }

    public RetInstr newRetInstr(Value retValue) {
        return new RetInstr(
                (retValue != null) ? retValue : new Value("", new VarType(0)),
                curBasicBlock
        );
    }

    public BrInstr newBRInstr(BasicBlock obj) {
        return new BrInstr(obj, curBasicBlock);
    }

    public BrInstr newBRInstr(BasicBlock obj, BasicBlock belong) {
        return new BrInstr(obj, belong);
    }

    public BrInstr newBRInstr(Value condValue, BasicBlock trueObj, BasicBlock falseObj) {
        return new BrInstr(condValue, trueObj, falseObj, curBasicBlock);
    }

    public IcmpInstr newIcmpInstr(String op, Value operand1, Value operand2) {
        IcmpInstr.IcmpType icmpType = (op.equals("==")) ?
                IcmpInstr.IcmpType.eq : (op.equals("!=")) ?
                IcmpInstr.IcmpType.ne : (op.equals("<")) ?
                IcmpInstr.IcmpType.slt : (op.equals("<=")) ?
                IcmpInstr.IcmpType.sle : (op.equals(">")) ?
                IcmpInstr.IcmpType.sgt : (op.equals(">=")) ?
                IcmpInstr.IcmpType.sge : null;

        int width1 = ((VarType) operand1.getValueType()).getWidth();
        int width2 = ((VarType) operand2.getValueType()).getWidth();
        if (width1 != 32) {
            operand1 = newZextInstr(operand1, new VarType(32));
            addInstr((Instr) operand1);
        }
        if (width2 != 32) {
            operand2 = newZextInstr(operand2, new VarType(32));
            addInstr((Instr) operand2);
        }
        return new IcmpInstr(LOCAL_VAR + (localVarCnt++), icmpType, operand1, operand2, curBasicBlock);
    }

    public ZextInstr newZextInstr(Value source, ValueType to) {
        return new ZextInstr(LOCAL_VAR + (localVarCnt++), to, source, curBasicBlock);
    }

    public PhiInstr newPhiInstr(AllocaInstr baseInstr, BasicBlock block, ArrayList<BasicBlock> preBlocks) {
        ValueType type = ((PointerType) baseInstr.getValueType()).getTargetType();
        return new PhiInstr(LOCAL_VAR + (localVarCnt++), type, baseInstr, preBlocks, block);
    }

    public PCopyInstr newPCopyInstr(BasicBlock belongBlock, Value from, Value to) {
        return new PCopyInstr(from, to, belongBlock);
    }

    public PAllocaInstr newPAllocaInstr(BasicBlock belongBlock, Value from) {
        return new PAllocaInstr(LOCAL_VAR + (localVarCnt++), from, belongBlock);
    }

    public void addInstr(Instr instr) {
        curBasicBlock.addInstr(instr);
    }

    // ---------------------------- 基本块上下文 -------------------------- //
    public IRBuilder addContext(BasicBlock block) {
        try {
            this.context.set(contextPos, block);
        } catch (IndexOutOfBoundsException e) {
            this.context.add(block);
        }
        contextPos += 1;
        return this;
    }

    public BasicBlock getLeaveBlock() {
        return context.get(contextPos - 1);    // 上下文最后一个是语句离开的基本块
    }

    public BasicBlock getFalseBlock() {
        return context.get(contextPos - 2);
    }

    public BasicBlock getIterBlock() {
        return context.get(contextPos - 2);
    }

    public BasicBlock getTrueBlock() {
        return context.get(contextPos - 3);
    }

    public void setTrueBlock(BasicBlock block) {
        context.set(contextPos - 3, block);
    }

    public void setFalseBlock(BasicBlock block) {
        context.set(contextPos - 2, block);
    }

    public void cleanContext(int cnt) {
        contextPos -= cnt;
    }
}
