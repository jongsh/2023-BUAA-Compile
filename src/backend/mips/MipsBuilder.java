package backend.mips;

import backend.mips.mipscmd.*;
import midend.llvmir.value.*;
import midend.llvmir.value.instr.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MipsBuilder {
    private static MipsBuilder instance = new MipsBuilder();
    private MipsProcedure procedure;
    private HashMap<Value, Reg> valueRegMap;        // value -- reg
    private HashMap<Value, Integer> valueStackMap;  // value -- stack
    private int stackOffset;  // 当前栈偏移($sp)
    private HashMap<String, LabelCmd> labelList;

    public static MipsBuilder getInstance() {
        return instance;
    }

    private MipsBuilder() {
        reFresh();
    }

    public void reFresh() {
        this.procedure = new MipsProcedure();
        this.valueRegMap = new HashMap<>();
        this.valueStackMap = new HashMap<>();
        this.labelList = new HashMap<>();
        this.stackOffset = 0;
    }

    public MipsProcedure getProcedure() {
        return procedure;
    }

    // 分配寄存器
    public void allocaRegs(Function function) {
        ArrayList<Reg> spareRegs = new ArrayList<>(Arrays.asList(
                Reg.$t0, Reg.$t1, Reg.$t2, Reg.$t3, Reg.$t4, Reg.$t5, Reg.$t6, Reg.$t7,
                Reg.$s0, Reg.$s1, Reg.$s2, Reg.$s3, Reg.$s4, Reg.$s5, Reg.$s6, Reg.$s7
        ));
        this.valueRegMap = new HashMap<>();
        this.valueStackMap = new HashMap<>();
        this.stackOffset = 0;
        // 首先为形参分配空间
        ArrayList<Param> params = function.getParamList();
        ArrayList<BasicBlock> blockList = function.getBasicBlockList();
        ArrayList<Instr> instrList = new ArrayList<>();
        for (BasicBlock block : blockList) {
            instrList.addAll(block.getInstrList());
        }

        int i = 0;
        ArrayList<Reg> aRegs = new ArrayList<>(Arrays.asList(Reg.$a1, Reg.$a2, Reg.$a3));
        for (i = 0; i < params.size() * 2; i += 2) {
            if (i < 6) {
                valueRegMap.put(instrList.get(i), aRegs.get(i / 2));
            } else {
                valueStackMap.put(instrList.get(i), stackOffset);
            }
            stackOffset -= 4;
        }
        for (; i < instrList.size(); ++i) {
            if (instrList.get(i) instanceof IcmpInstr || instrList.get(i).getName().equals("")
                    || valueRegMap.containsKey(instrList.get(i)) || valueStackMap.containsKey(instrList.get(i))) {
                continue;
            } else if (instrList.get(i) instanceof LoadInstr) {
                Value target = ((LoadInstr) instrList.get(i)).getTarget();
                if (valueRegMap.containsKey(target)) {
                    valueRegMap.put(instrList.get(i), valueRegMap.get(target));
                } else if (valueStackMap.containsKey(target)) {
                    valueStackMap.put(instrList.get(i), valueStackMap.get(target));
                }
                continue;
            } else if (instrList.get(i) instanceof AluInstr) {
                valueRegMap.put(instrList.get(i), Reg.$v1);
                continue;
            } else if (instrList.get(i) instanceof CallInstr) {
                valueRegMap.put(instrList.get(i), Reg.$v0);
                continue;
            }
            if (spareRegs.size() > 0) {
                valueRegMap.put(instrList.get(i), spareRegs.get(spareRegs.size() - 1));
                spareRegs.remove(spareRegs.size() - 1);
            } else {
                valueStackMap.put(instrList.get(i), stackOffset);
                stackOffset -= 4 * instrList.get(i).size();
            }
        }
    }

    // ----------------- 指令相关 --------------------- //
    private Reg takeRegOfValue(Value value, Reg backup, boolean handleDigit) {
        Reg retReg = null;
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
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, backup, Reg.$sp, valueStackMap.get(value)));
            retReg = backup;
        } else {
            String addrName = (value instanceof LoadInstr) ?
                    ((LoadInstr) value).getTarget().getName() : value.getName();
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
        if (labelList.containsKey(name)) {
            return labelList.get(name);
        } else {
            LabelCmd newLabel = new LabelCmd(name);
            labelList.put(name, newLabel);
            return newLabel;
        }
    }

    public void callInstrToCmd(String funcName, ArrayList<Value> arguments) {
        if (funcName.equals("putint")) {
            Reg argumentReg = takeRegOfValue(arguments.get(0), Reg.$a0, true);
            if (!argumentReg.equals(Reg.$a0)) {
                procedure.addTextCmd(new MoveCmd(Reg.$a0, argumentReg));
            }
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$v0, Reg.$zero, 1));
            procedure.addTextCmd(new SyscallCmd());
            return;
        } else if (funcName.equals("putstr")) {
            Reg argumentReg = takeRegOfValue(arguments.get(0), Reg.$a0, true);
            if (!argumentReg.equals(Reg.$a0)) {
                procedure.addTextCmd(new MoveCmd(Reg.$a0, argumentReg));
            }
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$v0, Reg.$zero, 4));
            procedure.addTextCmd(new SyscallCmd());
            return;
        }
        // 存寄存器: valueRegMap + ra
        procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$ra, Reg.$sp, stackOffset));
        stackOffset -= 4;
        ArrayList<Reg> regList = new ArrayList<>(valueRegMap.values());
        for (Reg reg : regList) {
            if (!reg.equals(Reg.$v0)) {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, reg, Reg.$sp, stackOffset));
                stackOffset -= 4;
            }
        }
        // 更改 sp
        procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$sp, Reg.$sp, stackOffset));
        int tempStackOffset = 0;
        // 函数传参
        ArrayList<Reg> aRegs = new ArrayList<>(Arrays.asList(Reg.$a1, Reg.$a2, Reg.$a3));
        for (int i = 0; i < arguments.size(); ++i) {
            Reg tempReg = takeRegOfValue(arguments.get(i), Reg.$t8, true);
            if (i < 3) {
                procedure.addTextCmd(new MoveCmd(aRegs.get(i), tempReg));
            } else {
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, tempReg, Reg.$sp, tempStackOffset));
            }
            tempStackOffset -= 4;
        }

        // 调用函数
        LabelCmd label = getLabelCmd(funcName);
        procedure.addTextCmd(new JumpCmd(JumpCmd.JumpCmdOp.jal, label));

        // 更改 sp
        procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$sp, Reg.$sp, -1 * stackOffset));
        // 恢复寄存器现场
        for (int i = regList.size() - 1; i >= 0; --i) {
            if (!regList.get(i).equals(Reg.$v0)) {
                stackOffset += 4;
                procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, regList.get(i), Reg.$sp, stackOffset));
            }
        }
        stackOffset += 4;
        procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.lw, Reg.$ra, Reg.$sp, stackOffset));
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

        if (valueRegMap.containsKey(to)) {
            procedure.addTextCmd(new MoveCmd(valueRegMap.get(to), fromReg));
        } else if (valueStackMap.containsKey(to)) {
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, fromReg, Reg.$sp, valueStackMap.get(to)));
        } else {
            procedure.addTextCmd(new LaCmd(Reg.$t8, to.getName().substring(1)));
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, fromReg, Reg.$t8, 0));
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
            Reg reg1 = takeRegOfValue(operands.get(i), Reg.$t8, true);
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$t9, Reg.$zero, dimensions.get(i)));
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.mul, Reg.$t9, reg1, Reg.$t9));
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addu, Reg.$v1, Reg.$v1, Reg.$t9));
        }
        if (basicLength > 1) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, Reg.$t8, Reg.$zero, basicLength));
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.mul, Reg.$v1, Reg.$v1, Reg.$t8));
        }
        // 计算地址
        Reg addrReg = takeAddrOfValue(basicValue, Reg.$t8);
        // 结果存入寄存器
        if (valueRegMap.containsKey(targetValue)) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addu, valueRegMap.get(targetValue), addrReg, Reg.$v1));
        } else if (valueStackMap.containsKey(targetValue)) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addu, Reg.$v1, Reg.$t8, Reg.$v1));
            procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, addrReg, Reg.$sp, valueStackMap.get(targetValue)));
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
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.subu, Reg.$v1, operandReg1, operandReg2));
                break;
            case "!=":
                aluCmdOp = AluCmd.AluCmdOp.xori;
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.subu, Reg.$v1, operandReg1, operandReg2));
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slti, Reg.$v1, Reg.$v1, 1));
                break;
            case ">=":
            case "<=":
                Reg opReg1 = cond.equals(">=") ? operandReg1 : operandReg2;
                Reg opReg2 = cond.equals(">=") ? operandReg2 : operandReg1;
                aluCmdOp = AluCmd.AluCmdOp.xori;
                procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, Reg.$v1, opReg2, opReg1));
                break;
            case ">":
            case "<":
                opReg1 = cond.equals(">") ? operandReg2 : operandReg1;
                opReg2 = cond.equals(">") ? operandReg1 : operandReg2;
                if (valueRegMap.containsKey(targetValue)) {
                    procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, valueRegMap.get(targetValue), opReg1, opReg2));
                } else if (valueStackMap.containsKey(targetValue)) {
                    procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.slt, Reg.$v1, operandReg1, operandReg2));
                    procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$v1, Reg.$sp, valueStackMap.get(targetValue)));
                }
                break;
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
        switch (op) {
            case "*":
                addMulCmd(sourceValue1, sourceValue2);
                break;
            case "/":
            case "%":
                addDivCmd(sourceValue1, sourceValue2);
                Reg targetReg = valueRegMap.getOrDefault(targetValue, Reg.$t8);
                procedure.addTextCmd(
                        new MfCmd((op.equals("/")) ? MfCmd.MfCmdOp.mflo : MfCmd.MfCmdOp.mfhi, targetReg)
                );
                if (targetReg.equals(Reg.$t8)) {
                    procedure.addTextCmd(new MemCmd(MemCmd.MemCmdOp.sw, Reg.$t8, Reg.$sp, valueStackMap.get(targetValue)));
                }
                break;
            case "+":
                addPlusCmd(sourceValue1, sourceValue2);
                break;
            case "-":
                addSubCmd(sourceValue1, sourceValue2);
                break;
            default:
                break;
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

    private void addMulCmd(Value sourceValue1, Value sourceValue2) {
        Reg targetReg = Reg.$v1;
        Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, true);
        Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, true);

        procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.mul, targetReg, sourceReg1, sourceReg2));
    }

    private void addDivCmd(Value sourceValue1, Value sourceValue2) {
        Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, true);
        Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, true);
        procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.div, null, sourceReg1, sourceReg2));
    }

    private void addPlusCmd(Value sourceValue1, Value sourceValue2) {
        Reg targetReg = Reg.$v1;   // 计算指令结果全放在临时寄存器中
        Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, false);
        Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, false);
        Integer immediate = null;
        if (sourceValue1 instanceof Digit) {
            immediate = ((Digit) sourceValue1).getNum();
        } else if (sourceValue2 instanceof Digit) {
            immediate = ((Digit) sourceValue2).getNum();
        }

        if (immediate != null) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, targetReg, (sourceReg1 != null) ? sourceReg1 : sourceReg2, immediate));
        } else {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addu, targetReg, sourceReg1, sourceReg2));
        }
    }

    private void addSubCmd(Value sourceValue1, Value sourceValue2) {
        Reg targetReg = Reg.$v1;   // 计算指令结果全放在临时寄存器中
        Reg sourceReg1 = takeRegOfValue(sourceValue1, Reg.$t8, false);
        Reg sourceReg2 = takeRegOfValue(sourceValue2, Reg.$t9, false);
        Integer immediate = null;
        if (sourceValue1 instanceof Digit) {
            immediate = ((Digit) sourceValue1).getNum();
        } else if (sourceValue2 instanceof Digit) {
            immediate = ((Digit) sourceValue2).getNum();
        }
        if (immediate != null) {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.addiu, targetReg, (sourceReg1 != null) ? sourceReg1 : sourceReg2, -1 * immediate));
        } else {
            procedure.addTextCmd(new AluCmd(AluCmd.AluCmdOp.subu, targetReg, sourceReg1, sourceReg2));
        }
    }

    public void addNoteCmd(String content) {
        procedure.addTextCmd(new NoteCmd(content));
    }

    public void addLabelCmd(String labelName) {
        procedure.addTextCmd(getLabelCmd(labelName));
    }

}
