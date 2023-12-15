package backend.mips;

import backend.mips.mipscmd.*;
import midend.llvmir.type.ArrayType;
import midend.llvmir.type.PointerType;
import midend.llvmir.value.*;
import midend.llvmir.value.Module;
import midend.llvmir.value.instr.*;
import util.CalTool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashSet;

public class MipsBuilder {
    private static MipsBuilder instance = new MipsBuilder();
    private MipsProcedure procedure;   // mips 汇编程序
    private LinkedHashMap<Value, Reg> valueRegMap;        // value -- reg
    private LinkedHashMap<Value, Integer> valueStackMap;  // value -- stack
    private int stackOffset;  // 当前栈偏移($sp)
    //private LinkedHashMap<String, LabelCmd> labelList;
    private RegAllocator regAllocator;
    private ArrayList<Reg> spareRegs = new ArrayList<>(Arrays.asList(
            Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3, Reg.$t4, Reg.$t5, Reg.$t6, Reg.$t7,
            Reg.$s0, Reg.$s1, Reg.$s2, Reg.$s3, Reg.$s4, Reg.$s5, Reg.$s6, Reg.$s7
    ));

    public static MipsBuilder getInstance() {
        return instance;
    }

    private MipsBuilder() {
        reFresh();
    }

    public void reFresh() {
        this.procedure = new MipsProcedure();
        this.valueRegMap = new LinkedHashMap<>();
        this.valueStackMap = new LinkedHashMap<>();
        //this.labelList = new LinkedHashMap<>();
        this.stackOffset = 0;
        this.regAllocator = new RegAllocator();
    }

    public MipsProcedure getProcedure() {
        return procedure;
    }

    // 分配寄存器
    public void allocaRegs(Module module) {
        regAllocator.alloca(module);
    }

    public void entryFunc(Function function) {
        String funcName = function.getName();
        this.valueRegMap = regAllocator.getFuncRegMap(funcName);
        this.valueStackMap = regAllocator.getFuncStackMap(funcName);
        this.stackOffset = regAllocator.getFuncStack(funcName);
    }
//    public void entryFunc(Function function) {
//        this.valueRegMap = new LinkedHashMap<>();
//        this.valueStackMap = new LinkedHashMap<>();
//        this.stackOffset = 0;
//
//        spareRegs = new ArrayList<>(Arrays.asList(
//                Reg.$a1, Reg.$a2, Reg.$a3,
//                Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3, Reg.$t4, Reg.$t5, Reg.$t6, Reg.$t7,
//                Reg.$s0, Reg.$s1, Reg.$s2, Reg.$s3, Reg.$s4, Reg.$s5, Reg.$s6, Reg.$s7
//        ));
//
//        ArrayList<Param> params = function.getParamList();
//        ArrayList<BasicBlock> blockList = function.getBasicBlockList();
//        ArrayList<Instr> instrList = new ArrayList<>();
//        for (BasicBlock block : blockList) {
//            instrList.addAll(block.getInstrList());
//        }
//        for (Param param : params) {
//            valueStackMap.put(param, stackOffset);
//            stackOffset -= 4;
//        }
//        for (Instr instr : instrList) {
//            if (instr instanceof IcmpInstr || instr.getName().equals("")
//                    || valueRegMap.containsKey(instr) || valueStackMap.containsKey(instr)) {
//                continue;
//            }
//            if (instr instanceof AllocaInstr &&
//                    ((PointerType) instr.getValueType()).getTargetType() instanceof ArrayType) {
//                continue;
//            }
//
//            if (spareRegs.size() > 0) {
//                valueRegMap.put(instr, spareRegs.get(spareRegs.size() - 1));
//                spareRegs.remove(spareRegs.size() - 1);
//            } else {
//                valueStackMap.put(instr, stackOffset);
//                stackOffset -= 4 * instr.size();
//            }
//        }
//    }

    // ----------------- 指令相关 --------------------- //
    private Reg takeRegOfValue(Value value, Reg backup, boolean handleDigit) {
        Reg retReg;
        // 取value到寄存器中，如果已经分配了全局寄存器则直接使用，否则存到backup中
        // 对于数字比较特别，如果标志handleDigit为真，为数字分配临时寄存器
        if (value instanceof Digit) {
            retReg = Reg.$zero;
            if (handleDigit && ((Digit) value).getNum() != 0) {
                procedure.addTextCmd(
                        new AluCmd(AluCmd.AluCmdOp.addiu, backup, Reg.$zero, ((Digit) value).getNum())
                );
                retReg = backup;
            }
        } else if (valueRegMap.containsKey(value)) {
            retReg = valueRegMap.get(value);
        } else if (valueStackMap.containsKey(value)) {
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, backup, Reg.$sp, valueStackMap.get(value)));
            retReg = backup;
        } else {
            String addrName = (value instanceof LoadInstr) ?
                    ((LoadInstr) value).getTarget().getName() : value.getName();
            procedure.addTextCmd(new LaCmd(backup, addrName.substring(1)));
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, backup, backup, 0));
            retReg = backup;
        }
        return retReg;
    }

    private Reg takeAddrOfValue(Value value, Reg backup) {
        Reg retReg;
        if (valueRegMap.containsKey(value)) {
            retReg = valueRegMap.get(value);
        } else if (valueStackMap.containsKey(value)) {
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, backup, Reg.$sp, valueStackMap.get(value)));
            retReg = backup;
        } else {
            String addrName = value.getName();
            procedure.addTextCmd(new LaCmd(backup, addrName.substring(1)));
            retReg = backup;
        }
        return retReg;
    }

    public void globalStrToCmd(String name, String content) {
        procedure.addDataCmd(new GlobalStrCmd(name, content));
    }

    public void globalVarToCmd(String name, ArrayList<Integer> initials) {
        procedure.addDataCmd(new GlobalVarCmd(name, initials));
    }

    public LabelCmd getLabelCmd(String name) {
        return new LabelCmd(name);
    }

    public void allocaInstrToCmd(Value target, int size) {
        stackOffset -= size * 4;
        valueStackMap.put(target, stackOffset);
        procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$v1, Reg.$sp, stackOffset + 4));
        procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, stackOffset));
        stackOffset -= 4;
    }

    public void callInstrToCmd(Value targetValue, String funcName, ArrayList<Value> arguments) {
        switch (funcName) {
            case "putint": {
                Reg argumentReg = takeRegOfValue(arguments.get(0), Reg.$a0, true);
                if (!argumentReg.equals(Reg.$a0)) {
                    procedure.addTextCmd(new MoveCmd(Reg.$a0, argumentReg));
                }
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$v0, Reg.$zero, 1));
                procedure.addTextCmd(new SyscallCmd());
                break;
            }
            case "putstr": {
                Reg argumentReg = takeRegOfValue(arguments.get(0), Reg.$a0, true);
                if (!argumentReg.equals(Reg.$a0)) {
                    procedure.addTextCmd(new MoveCmd(Reg.$a0, argumentReg));
                }
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$v0, Reg.$zero, 4));
                procedure.addTextCmd(new SyscallCmd());
                break;
            }
            case "getint":
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$v0, Reg.$zero, 5));
                procedure.addTextCmd(new SyscallCmd());
                break;
            default:
                // 存寄存器: usedRegsMap + ra
                HashSet<Reg> usedRegsSet = regAllocator.getFuncUsedRegs("@" + funcName);
                usedRegsSet.add(Reg.$ra);
                LinkedHashMap<Reg, Integer> storeLocation = new LinkedHashMap<>();
                for (Reg reg : usedRegsSet) {
                    procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, reg, Reg.$sp, stackOffset));
                    storeLocation.put(reg, stackOffset);
                    stackOffset -= 4;
                }

                // 函数传参
                int tempStackOffset = stackOffset;
                ArrayList<Reg> paramRegList = regAllocator.getFuncParamRegList("@" + funcName);

                ArrayList<Reg> replaceRegList = new ArrayList<>(Arrays.asList(Reg.$t8, Reg.$t9, Reg.$v1));
                HashSet<Reg> toARegSet = new HashSet<>();
                LinkedHashMap<Reg, Reg> fromARegMap = new LinkedHashMap<>();
                for (int i = 0; i < arguments.size(); ++i) {
                    if (paramRegList.get(i) != null) {
                        toARegSet.add(paramRegList.get(i));  // 记录目标的寄存器
                    }
                    fromARegMap.put(valueRegMap.getOrDefault(arguments.get(i), null), null);
                }
                int replaceIndex = 0;
                for (Reg fromAReg : fromARegMap.keySet()) {
                    if (toARegSet.contains(fromAReg)) {
                        procedure.addTextCmd(new MoveCmd(replaceRegList.get(replaceIndex), fromAReg));
                        fromARegMap.put(fromAReg, replaceRegList.get(replaceIndex));
                        replaceIndex++;
                    }
                }
                for (int i = 0; i < arguments.size(); ++i) {
                    if (paramRegList.get(i) != null) {
                        Reg tempReg;
                        if (arguments.get(i) instanceof Digit) {
                            procedure.addTextCmd(
                                    new AluCmd(AluCmd.AluCmdOp.addiu, paramRegList.get(i), Reg.$zero, ((Digit) arguments.get(i)).getNum())
                            );
                        } else if (valueRegMap.containsKey(arguments.get(i))) {
                            tempReg = valueRegMap.get(arguments.get(i));
                            if (fromARegMap.getOrDefault(tempReg, null) != null) {
                                procedure.addTextCmd(new MoveCmd(paramRegList.get(i), fromARegMap.get(tempReg)));
                            } else {
                                procedure.addTextCmd(new MoveCmd(paramRegList.get(i), tempReg));
                            }
                        } else if (valueStackMap.containsKey(arguments.get(i))) {
                            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, paramRegList.get(i), Reg.$sp,
                                    valueStackMap.get(arguments.get(i))));
                        } else {
                            String addrName = arguments.get(i).getName();
                            procedure.addTextCmd(new LaCmd(paramRegList.get(i), addrName.substring(1)));
                            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, paramRegList.get(i), paramRegList.get(i), 0));
                        }
                    } else {
                        Reg tempReg = takeRegOfValue(arguments.get(i), Reg.$t8, true);
                        if (fromARegMap.getOrDefault(tempReg, null) != null) {
                            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, fromARegMap.get(tempReg), Reg.$sp, tempStackOffset));
                        } else {
                            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, tempReg, Reg.$sp, tempStackOffset));
                        }
                        tempStackOffset -= 4;
                    }
                }
                // 更改 sp
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$sp, Reg.$sp, stackOffset));

                // 调用函数
                LabelCmd label = getLabelCmd(funcName);
                procedure.addTextCmd(new JumpCmd(JumpCmd.JumpCmdOp.jal, label));

                // 更改 sp
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$sp, Reg.$sp, -1 * stackOffset));
                // 恢复寄存器现场
                for (Reg reg : storeLocation.keySet()) {
                    procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, reg, Reg.$sp, storeLocation.get(reg)));
                }
                break;
        }

        // 存储函数返回值
        if (valueRegMap.containsKey(targetValue)) {
            procedure.addTextCmd(new MoveCmd(valueRegMap.get(targetValue), Reg.$v0));
        } else if (valueStackMap.containsKey(targetValue)) {
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v0, Reg.$sp, valueStackMap.get(targetValue)));
        }
    }

    public void retInstrToCmd(String funcName, Value retValue) {
        if (funcName.equals("main")) {
            procedure.addTextCmd(new JumpCmd(JumpCmd.JumpCmdOp.j, getLabelCmd("end")));
        } else {
            if (retValue != null) {
                Reg retReg = takeRegOfValue(retValue, Reg.$t8, true);
                procedure.addTextCmd(new MoveCmd(Reg.$v0, retReg));
            }
            procedure.addTextCmd(new JumpCmd(JumpCmd.JumpCmdOp.jr, Reg.$ra));
        }
    }

    public void storeInstrToCmd(Value from, Value to) {
        Reg fromReg = takeRegOfValue(from, Reg.$t9, true);

        if (to instanceof GepInstr) {
            if (valueRegMap.containsKey(to)) {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, fromReg, valueRegMap.get(to), 0));
            } else if (valueStackMap.containsKey(to)) {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, Reg.$t8, Reg.$sp, valueStackMap.get(to)));
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, fromReg, Reg.$t8, 0));
            }
        } else {
            if (valueRegMap.containsKey(to)) {
                procedure.addTextCmd(new MoveCmd(valueRegMap.get(to), fromReg));
            } else if (valueStackMap.containsKey(to)) {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, fromReg, Reg.$sp, valueStackMap.get(to)));
            } else {
                procedure.addTextCmd(new LaCmd(Reg.$t8, to.getName().substring(1)));
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, fromReg, Reg.$t8, 0));
            }
        }
    }

    public void loadInstrToCmd(Value from, Value to) {
        Reg toReg = valueRegMap.getOrDefault(to, Reg.$t9);
        if (valueRegMap.containsKey(from)) {
            if (toReg.equals(Reg.$t9)) {
                procedure.addTextCmd(
                        new MemCmd(MemCmd.MemCmdOp.lw, Reg.$t8, valueRegMap.get(from), 0)
                );
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$t8, Reg.$sp, valueStackMap.get(to)));
            } else {
                procedure.addTextCmd(
                        new MemCmd(MemCmd.MemCmdOp.lw, toReg, valueRegMap.get(from), 0)
                );
            }
        } else if (valueStackMap.containsKey(from)) {
            procedure.addTextCmd(
                    new MemCmd(MemCmd.MemCmdOp.lw, Reg.$t8, Reg.$sp, valueStackMap.get(from))
            );
            if (toReg.equals(Reg.$t9)) {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, Reg.$t8, Reg.$t8, 0));
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$t8, Reg.$sp, valueStackMap.get(to)));
            } else {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, toReg, Reg.$t8, 0));
            }
        } else {
            String addrName = ((LoadInstr) to).getTarget().getName();
            procedure.addTextCmd(new LaCmd(Reg.$t8, addrName.substring(1)));
            if (toReg.equals(Reg.$t9)) {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, Reg.$t8, Reg.$t8, 0));
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$t8, Reg.$sp, valueStackMap.get(to)));
            } else {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, toReg, Reg.$t8, 0));
            }
        }


    }

    public void gepInstrToCmd(Value targetValue, Value basicValue, int basicLength,
                              ArrayList<Value> operands, ArrayList<Integer> dimensions) {
        for (int i = dimensions.size() - 2; i >= 0; --i) {
            dimensions.set(i, dimensions.get(i) * dimensions.get(i + 1));
        }
        // v1 存 offset
        procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$v1, Reg.$zero, 0));
        for (int i = 0; i < operands.size(); ++i) {
            if (operands.get(i) instanceof Digit && ((Digit) operands.get(i)).getNum() == 0) {
                continue;
            } else if (operands.get(i) instanceof Digit) {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$v1, Reg.$v1,
                        dimensions.get(i) * ((Digit) operands.get(i)).getNum()));
                continue;
            }
            Reg reg1 = takeRegOfValue(operands.get(i), Reg.$t8, true);
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$t9, Reg.$zero, dimensions.get(i)));
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.mul, Reg.$t9, reg1, Reg.$t9));
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addu, Reg.$v1, Reg.$v1, Reg.$t9));
        }
        if (basicLength > 1) {
            if (CalTool.getPowerOfTwo(basicLength) != -1) {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.sll, Reg.$v1, Reg.$v1, CalTool.getPowerOfTwo(basicLength)));
            } else {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$t8, Reg.$zero, basicLength));
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.mul, Reg.$v1, Reg.$v1, Reg.$t8));
            }
        }
        // 计算地址
        Reg addrReg = takeAddrOfValue(basicValue, Reg.$t8);
        // 结果存入寄存器
        if (valueRegMap.containsKey(targetValue)) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addu, valueRegMap.get(targetValue), addrReg, Reg.$v1));
        } else if (valueStackMap.containsKey(targetValue)) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addu, Reg.$v1, addrReg, Reg.$v1));
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
        }
    }

    public void brInstrToCmd(String cond, Value operand1, Value operand2, String tureName, String falseName) {
        LabelCmd trueLabel = getLabelCmd(tureName);
        LabelCmd falseLabel = getLabelCmd(falseName);
        Reg operandReg1 = takeRegOfValue(operand1, Reg.$t8, true);
        Reg operandReg2 = takeRegOfValue(operand2, Reg.$t9, true);
        switch (cond) {
            case "==":
                procedure.addTextCmd(new BranchCmd(BranchCmd.BranchCmdOp.beq, operandReg1, operandReg2, trueLabel));
                break;
            case "!=":
                procedure.addTextCmd(new BranchCmd(BranchCmd.BranchCmdOp.bne, operandReg1, operandReg2, trueLabel));
                break;
            case ">=":
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, Reg.$v1, operandReg1, operandReg2));
                procedure.addTextCmd(new BranchCmd(BranchCmd.BranchCmdOp.beq, Reg.$v1, Reg.$zero, trueLabel));
                break;
            case "<=":
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, Reg.$v1, operandReg2, operandReg1));
                procedure.addTextCmd(new BranchCmd(BranchCmd.BranchCmdOp.beq, Reg.$v1, Reg.$zero, trueLabel));
                break;
            case ">":
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, Reg.$v1, operandReg2, operandReg1));
                procedure.addTextCmd(new BranchCmd(BranchCmd.BranchCmdOp.bne, Reg.$v1, Reg.$zero, trueLabel));
                break;
            case "<":
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, Reg.$v1, operandReg1, operandReg2));
                procedure.addTextCmd(new BranchCmd(BranchCmd.BranchCmdOp.bne, Reg.$v1, Reg.$zero, trueLabel));
                break;
            default:
                break;
        }
        procedure.addTextCmd(new JumpCmd(JumpCmd.JumpCmdOp.j, falseLabel));
    }

    public void zextInstrToCmd(String cond, Value targetValue, Value operand1, Value operand2) {
        Reg operandReg1 = takeRegOfValue(operand1, Reg.$t8, true);
        Reg operandReg2 = takeRegOfValue(operand2, Reg.$t9, true);
        AluCmd.AluCmdOp aluCmdOp = null;
        switch (cond) {
            case "==":
                aluCmdOp = AluCmd.AluCmdOp.slti;
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.xor, Reg.$v1, operandReg1, operandReg2));
                break;
            case "!=":
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.xor, Reg.$v1, operandReg1, operandReg2));
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.sltu, Reg.$v1, Reg.$zero, Reg.$v1));
                if (valueRegMap.containsKey(targetValue)) {
                    procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.sltu, valueRegMap.get(targetValue), Reg.$zero, Reg.$v1));
                } else if (valueStackMap.containsKey(targetValue)) {
                    procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.sltu, Reg.$v1, Reg.$zero, Reg.$v1));
                    procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
                }
                return;
            case ">=":
            case "<=":
                Reg opReg1 = cond.equals(">=") ? operandReg1 : operandReg2;
                Reg opReg2 = cond.equals(">=") ? operandReg2 : operandReg1;
                aluCmdOp = AluCmd.AluCmdOp.xori;
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, Reg.$v1, opReg1, opReg2));
                break;
            case ">":
            case "<":
                opReg1 = cond.equals(">") ? operandReg2 : operandReg1;
                opReg2 = cond.equals(">") ? operandReg1 : operandReg2;
                if (valueRegMap.containsKey(targetValue)) {
                    procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, valueRegMap.get(targetValue), opReg1, opReg2));
                } else if (valueStackMap.containsKey(targetValue)) {
                    procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, Reg.$v1, opReg1, opReg2));
                    procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
                }
                return;
            default:
                break;
        }
        if (valueRegMap.containsKey(targetValue)) {
            procedure.addTextCmd(new AluCmd(aluCmdOp, valueRegMap.get(targetValue), Reg.$v1, 1));
        } else if (valueStackMap.containsKey(targetValue)) {
            procedure.addTextCmd(new AluCmd(aluCmdOp, Reg.$v1, Reg.$v1, 1));
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
        }
    }

    public void aluInstrToCmd(String op, Value targetValue, Value sourceValue1, Value sourceValue2) {
        Reg targetReg = valueRegMap.getOrDefault(targetValue, Reg.$t8);
        switch (op) {
            case "*":
                addMulCmd(sourceValue1, sourceValue2, targetValue);
                return;
            case "/":
            case "%":
                addDivCmd(sourceValue1, sourceValue2);
                procedure.addTextCmd(
                        new MfCmd((op.equals("/")) ? MfCmd.MfCmdOp.mflo : MfCmd.MfCmdOp.mfhi, targetReg)
                );
                if (targetReg.equals(Reg.$t8)) {
                    procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$t8, Reg.$sp, valueStackMap.get(targetValue)));
                }
                return;
            case "+":
                addPlusCmd(sourceValue1, sourceValue2, targetValue);
                return;
            case "-":
                addSubCmd(sourceValue1, sourceValue2, targetValue);
                return;
        }
    }

    public void addJumpCmd(String name, boolean functionCall) {
        if (name == null) {
            procedure.addTextCmd(new JumpCmd(JumpCmd.JumpCmdOp.jr, Reg.$ra));
        } else if (functionCall) {
            procedure.addTextCmd(new JumpCmd(JumpCmd.JumpCmdOp.jal, getLabelCmd(name)));
        } else {
            procedure.addTextCmd(new JumpCmd(JumpCmd.JumpCmdOp.j, getLabelCmd(name)));
        }
    }

    private void addMulCmd(Value sourceValue1, Value sourceValue2, Value targetValue) {
        Reg targetReg = valueRegMap.getOrDefault(targetValue, Reg.$t8);
        if (sourceValue1 instanceof Digit && CalTool.getPowerOfTwo(((Digit) sourceValue1).getNum()) != -1) {
            Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, true);
            if (targetReg.equals(Reg.$t8)) {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.sll, Reg.$v1, sourceReg2, CalTool.getPowerOfTwo(((Digit) sourceValue1).getNum())));
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
            } else {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.sll, targetReg, sourceReg2, CalTool.getPowerOfTwo(((Digit) sourceValue1).getNum())));
            }

        } else if (sourceValue2 instanceof Digit && CalTool.getPowerOfTwo(((Digit) sourceValue2).getNum()) != -1) {
            Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, true);
            if (targetReg.equals(Reg.$t8)) {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.sll, Reg.$v1, sourceReg1, CalTool.getPowerOfTwo(((Digit) sourceValue2).getNum())));
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
            } else {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.sll, targetReg, sourceReg1, CalTool.getPowerOfTwo(((Digit) sourceValue2).getNum())));
            }
        } else {
            Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, true);
            Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, true);
            if (targetReg.equals(Reg.$t8)) {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.mul, Reg.$v1, sourceReg1, sourceReg2));
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
            } else {
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.mul, targetReg, sourceReg1, sourceReg2));
            }
        }

    }

    private void addDivCmd(Value sourceValue1, Value sourceValue2) {
        Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, true);
        Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, true);
        procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.div, null, sourceReg1, sourceReg2));
    }

    private void addPlusCmd(Value sourceValue1, Value sourceValue2, Value targetValue) {
        Reg targetReg = valueRegMap.getOrDefault(targetValue, Reg.$v1);
        Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, true);
        Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, false);
        Integer immediate = null;
        if (sourceValue2 instanceof Digit) {
            immediate = ((Digit) sourceValue2).getNum();
        }

        if (immediate != null) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, targetReg, (sourceReg1 != null) ? sourceReg1 : sourceReg2, immediate));
        } else {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addu, targetReg, sourceReg1, sourceReg2));
        }

        if (targetReg.equals(Reg.$v1)) {
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
        }
    }

    private void addSubCmd(Value sourceValue1, Value sourceValue2, Value targetValue) {
        Reg targetReg = valueRegMap.getOrDefault(targetValue, Reg.$v1);
        Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, true);
        Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, false);
        Integer immediate = null;
        if (sourceValue2 instanceof Digit) {
            immediate = ((Digit) sourceValue2).getNum();
        }
        if (immediate != null) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, targetReg, (sourceReg1 != null) ? sourceReg1 : sourceReg2, -1 * immediate));
        } else {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.subu, targetReg, sourceReg1, sourceReg2));
        }

        if (targetReg.equals(Reg.$v1)) {
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
        }
    }

    public void addNoteCmd(String content) {
        procedure.addTextCmd(new NoteCmd(content));
    }

    public void addLabelCmd(String labelName) {
        procedure.addTextCmd(getLabelCmd(labelName));
    }

}
