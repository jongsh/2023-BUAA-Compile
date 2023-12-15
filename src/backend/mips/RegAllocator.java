package backend.mips;

import midend.llvmir.type.ArrayType;
import midend.llvmir.type.PointerType;
import midend.llvmir.value.Module;
import midend.llvmir.value.*;
import midend.llvmir.value.instr.AllocaInstr;
import midend.llvmir.value.instr.IcmpInstr;
import midend.llvmir.value.instr.Instr;
import midend.llvmir.value.instr.ZextInstr;
import midend.optimize.CFG;
import util.CalTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashSet;

public class RegAllocator {
    public static class AllocaRecord {
        private final ArrayList<Reg> paramRegList;      // 函数形参的寄存器分配
        private final LinkedHashMap<Value, Reg> valueRegMap;        // value -- reg
        private final LinkedHashMap<Value, Integer> valueStackMap;  // value -- stack
        private int stackOffset;  // 当前栈偏移($sp)
        private ArrayList<Reg> spareRegs;

        public AllocaRecord() {
            this.stackOffset = 0;
            this.paramRegList = new ArrayList<>();
            this.valueRegMap = new LinkedHashMap<>();
            this.valueStackMap = new LinkedHashMap<>();
            this.spareRegs = new ArrayList<>(Arrays.asList(
                    Reg.$k0, Reg.$k1,
                    Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3, Reg.$t4, Reg.$t5, Reg.$t6, Reg.$t7,
                    Reg.$s0, Reg.$s1, Reg.$s2, Reg.$s3, Reg.$s4, Reg.$s5, Reg.$s6, Reg.$s7,
                    Reg.$a3, Reg.$a2, Reg.$a1));
        }

        public void addValue(Value value) {
            if (!valueRegMap.containsKey(value) && !valueStackMap.containsKey(value)) {
                if (spareRegs.size() > 0) {
                    valueRegMap.put(value, spareRegs.get(spareRegs.size() - 1));
                    spareRegs.remove(spareRegs.size() - 1);

                } else {
                    valueStackMap.put(value, stackOffset);
                    stackOffset -= 4;
                }
            }
        }

        public void addParam(Value param, boolean isToReg) {
            if (isToReg) {
                valueRegMap.put(param, spareRegs.get(spareRegs.size() - 1));
                paramRegList.add(spareRegs.get(spareRegs.size() - 1));
                spareRegs.remove(spareRegs.size() - 1);
            } else {
                valueStackMap.put(param, stackOffset);
                paramRegList.add(null);
                stackOffset -= 4;
            }
        }

        public void release(Value value) {
            if (valueRegMap.containsKey(value)) {
                if (!spareRegs.contains(valueRegMap.get(value))) {
                    spareRegs.add(valueRegMap.get(value));
                }
            }
        }

        public void maintain(HashSet<Value> stores) {
            HashSet<Reg> storedRegs = new HashSet<>();
            // 统计哪些寄存器需要保存的
            for (Value storeValue : stores) {
                if (valueRegMap.containsKey(storeValue)) {
                    storedRegs.add(valueRegMap.get(storeValue));
                }
            }
            // 不需要的寄存器释放
            for (Value value : valueRegMap.keySet()) {
                if (!storedRegs.contains(valueRegMap.get(value))) {
                    release(value);
                }
            }
            // 需要的寄存器保留
            ArrayList<Reg> newSpareRegs = new ArrayList<>();
            for (Reg spareReg : spareRegs) {
                if (!storedRegs.contains(spareReg)) {
                    newSpareRegs.add(spareReg);
                }
            }
            this.spareRegs = newSpareRegs;
        }

        public ArrayList<Reg> getParamRegList() {
            return paramRegList;
        }

        public LinkedHashMap<Value, Reg> getValueRegMap() {
            return valueRegMap;
        }

        public LinkedHashMap<Value, Integer> getValueStackMap() {
            return valueStackMap;
        }

        public int getStackOffset() {
            return stackOffset;
        }
    }

    private final LinkedHashMap<String, AllocaRecord> funcRecMaps;
    // 中间值
    private Function curFunction;
    private CFG curCFG;
    private LinkedHashMap<Instr, HashSet<Value>> curInstrActiveRec;
    private LinkedHashMap<BasicBlock, HashSet<Value>> curBlockActiveRec;
    private HashSet<Value> entryActiveRec;

    public RegAllocator() {
        this.funcRecMaps = new LinkedHashMap<>();
    }

    public void alloca(Module module) {
        ArrayList<Function> functionList = module.getFunctionList();
        for (Function function : functionList) {
            funcRecMaps.put(function.getName(), new AllocaRecord());
            curFunction = function;
            curCFG = new CFG(function);
            curInstrActiveRec = new LinkedHashMap<>();
            curBlockActiveRec = new LinkedHashMap<>();
            entryActiveRec = new HashSet<>();

            // 数据流分析
            ArrayList<BasicBlock> blockList = function.getBasicBlockList();
            for (int i = blockList.size() - 1; i >= 0; --i) {
                if (!curBlockActiveRec.containsKey(blockList.get(i))) {
                    curBlockActiveRec.put(blockList.get(i), new HashSet<>());
                    dataStreamAnalyse(blockList.get(i));
                }
            }
            // 分配寄存器
            allocaRegs(function);
        }
    }

    private void allocaRegs(Function function) {
        ArrayList<Param> params = function.getParamList();
        ArrayList<BasicBlock> blockList = function.getBasicBlockList();
        // 函数入口为param分配寄存器
        for (int i = 0; i < params.size(); ++i) {
            funcRecMaps.get(function.getName()).addParam(params.get(i), i < 3);
        }
        for (BasicBlock block : blockList) {
            ArrayList<Instr> instrList = block.getInstrList();
            for (int i = 0; i < instrList.size(); ++i) {
                Instr instr = instrList.get(i);
                if (i == 0) {
                    funcRecMaps.get(function.getName()).maintain(CalTool.add(
                            curInstrActiveRec.get(instr), new HashSet<>(instr.getOperands()))
                    );
                } else {
                    for (Value used : instr.getOperands()) {
                        if (used instanceof Instr && !curInstrActiveRec.get(instr).contains(used)) {
                            funcRecMaps.get(function.getName()).release(used);
                        }
                    }
                }
                if (instr instanceof IcmpInstr) {
                    continue;
                }
                if (instr instanceof AllocaInstr &&
                        ((PointerType) instr.getValueType()).getTargetType() instanceof ArrayType) {
                    continue;
                }
                if (i == 0) {
                    funcRecMaps.get(function.getName()).maintain(CalTool.add(
                            curInstrActiveRec.get(instr), new HashSet<>(instr.getOperands()))
                    );
                } else {
                    for (Value used : instr.getOperands()) {
                        if (used instanceof Instr && !curInstrActiveRec.get(instr).contains(used)) {
                            funcRecMaps.get(function.getName()).release(used);
                        }
                    }
                }
                if (!instr.getName().equals("")) {
                    funcRecMaps.get(function.getName()).addValue(instr);
                }
            }
        }
    }

    private void dataStreamAnalyse(BasicBlock entry) {
        // 从最后一个基本块开始倒序分析数据流，分析变量活跃程度
        ArrayList<Instr> instrList = entry.getInstrList();
        HashSet<Value> out = curBlockActiveRec.get(entry);
        HashSet<Value> in = new HashSet<>();
        for (int i = instrList.size() - 1; i >= 0; --i) {
            curInstrActiveRec.put(instrList.get(i), out);
            HashSet<Value> used = new HashSet<>(instrList.get(i).getOperands());
            if (instrList.get(i).getName().equals("")) {
                in = CalTool.add(used, out);
            } else if (instrList.get(i) instanceof ZextInstr) {
                IcmpInstr icmpInstr = (IcmpInstr) instrList.get(i).getOperands().get(0);
                used.addAll(icmpInstr.getOperands());
                in = CalTool.add(used, CalTool.sub(out, instrList.get(i)));
            } else {
                in = CalTool.add(used, CalTool.sub(out, instrList.get(i)));
            }
            out = in;
        }
        // 此时 in 集合就是 entry 入口活跃变量
        if (curCFG.getCFGFatherList(entry).size() == 0) {
            entryActiveRec = CalTool.add(entryActiveRec, in);
            return;
        }
        for (BasicBlock beforeBlock : curCFG.getCFGFatherList(entry)) {
            HashSet<Value> tempActive = curBlockActiveRec.getOrDefault(beforeBlock, new HashSet<>());
            HashSet<Value> newActive = CalTool.add(tempActive, in);
            if (newActive.size() > tempActive.size()) {
                curBlockActiveRec.put(beforeBlock, newActive);
                dataStreamAnalyse(beforeBlock);
            }
        }
    }

    public ArrayList<Reg> getFuncParamRegList(String funcName) {
        return funcRecMaps.get(funcName).getParamRegList();
    }

    public LinkedHashMap<Value, Reg> getFuncRegMap(String funcName) {
        return funcRecMaps.get(funcName).getValueRegMap();
    }

    public LinkedHashMap<Value, Integer> getFuncStackMap(String funcName) {
        return funcRecMaps.get(funcName).getValueStackMap();
    }

    public int getFuncStack(String funcName) {
        return funcRecMaps.get(funcName).getStackOffset();
    }

    public HashSet<Reg> getFuncUsedRegs(String funcName) {
        return new HashSet<>(funcRecMaps.get(funcName).getValueRegMap().values());
    }
}
