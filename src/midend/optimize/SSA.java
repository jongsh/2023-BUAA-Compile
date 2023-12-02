package midend.optimize;

import midend.llvmir.IRBuilder;
import midend.llvmir.type.ArrayType;
import midend.llvmir.type.PointerType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Function;
import midend.llvmir.value.Value;
import midend.llvmir.value.instr.*;

import java.util.*;

public class SSA {
    private static Function function;
    private static CFG cfg;

    public static void transToSSA(Function f, CFG c) {
        function = f;
        cfg = c;
        HashMap<Value, Stack<Value>> varValueMap = new HashMap<>();
        HashSet<BasicBlock> defBlocks = new HashSet<>();
        // 遍历函数的每个 alloca 定义，转化成 SSA
        for (BasicBlock block : function.getBasicBlockList()) {
            for (Instr instr : block.getInstrList()) {
                if (instr instanceof AllocaInstr && !(((PointerType) instr.getValueType()).getTargetType() instanceof ArrayType)) {
                    // 获取变量定义基本块
                    defBlocks.add(instr.getBelong());
                    for (Value user : instr.getUserList()) {
                        if (user instanceof StoreInstr && ((StoreInstr) user).getTo().equals(instr)) {
                            defBlocks.add(((StoreInstr) user).getBelong());
                        }
                    }
                    // 根据定义块插入 phi 指令
                    insertPhi(defBlocks, (AllocaInstr) instr);
                    // 填入 map
                    varValueMap.put(instr, new Stack<>());
                    // 重置定义块
                    defBlocks = new HashSet<>();
                }
            }
        }
        // 变量重命名
        dfsToRename(function.getBasicBlockList().get(0), varValueMap);
    }

    // 根据定义块插入 phi
    private static void insertPhi(HashSet<BasicBlock> defBlocks, AllocaInstr baseInstr) {
        Stack<BasicBlock> defBlockStack = new Stack<>();
        HashSet<BasicBlock> insertedBlocks = new HashSet<>();
        for (BasicBlock defBlock : defBlocks) {
            defBlockStack.push(defBlock);
        }
        while (!defBlockStack.isEmpty()) {
            BasicBlock temp = defBlockStack.pop();
            for (BasicBlock df : cfg.getDFListOf(temp)) {
                if (!insertedBlocks.contains(df)) {
                    insertedBlocks.add(df);
                    df.getInstrList().add(
                            0, IRBuilder.getInstance().newPhiInstr(baseInstr, df, cfg.getCFGFatherList(df))
                    );
                    if (!defBlocks.contains(df)) {
                        defBlockStack.push(df);
                    }
                }
            }
        }
    }

    private static void dfsToRename(BasicBlock curBlock, HashMap<Value, Stack<Value>> varValueMap) {
        if (curBlock.getName().equals("block_22")) {
            int m = 1;
        }
        // 遍历当前块的指令，更新 map 信息
        HashMap<Value, Integer> cntMap = new HashMap<>();
        ArrayList<Instr> instrList = curBlock.getInstrList();
        for (int i = 0; i < instrList.size(); ++i) {
            Instr curInstr = instrList.get(i);
            if (curInstr instanceof AllocaInstr && varValueMap.containsKey(curInstr)) {
                instrList.remove(i);
                i--;
            } else if (curInstr instanceof StoreInstr) {
                Value toValue = ((StoreInstr) curInstr).getTo();
                Value fromValue = ((StoreInstr) curInstr).getFrom();
                if (varValueMap.containsKey(toValue)) {
                    if (varValueMap.containsKey(fromValue)) {
                        varValueMap.get(toValue).push(myStackPeek(varValueMap.get(fromValue)));
                    } else {
                        varValueMap.get(toValue).push(fromValue);
                    }
                    cntMap.put(toValue, cntMap.containsKey(toValue) ? cntMap.get(toValue) + 1 : 1);
                    instrList.remove(i);
                    i--;
                } else if (varValueMap.containsKey(fromValue)) {
                    ((Instr) toValue).modifyOperand(fromValue, myStackPeek(varValueMap.get(fromValue)));
                }
            } else if (curInstr instanceof LoadInstr) {
                Value loadFromValue = ((LoadInstr) curInstr).getTarget();
                if (varValueMap.containsKey(loadFromValue)) {
                    curInstr.modifyToNewValue(myStackPeek(varValueMap.get(loadFromValue)));
                    instrList.remove(i);
                    i--;
                }
            } else if (curInstr instanceof PhiInstr) {
                Value baseValue = ((PhiInstr) curInstr).getBaseInstr();
                if (varValueMap.containsKey(baseValue)) {
                    varValueMap.get(baseValue).push(curInstr);
                    cntMap.put(baseValue, cntMap.containsKey(baseValue) ? cntMap.get(baseValue) + 1 : 1);
                }
            }
        }
        // 遍历CFG后继块填入 phi
        ArrayList<BasicBlock> cfgAfterBlocks = cfg.getCFGChildrenList(curBlock);
        for (BasicBlock afterBlock : cfgAfterBlocks) {
            for (Instr instr : afterBlock.getInstrList()) {
                if (instr instanceof PhiInstr) {
                    Value baseValue = ((PhiInstr) instr).getBaseInstr();
                    if (varValueMap.get(baseValue).size() > 0) {
                        ((PhiInstr) instr).fillIn(curBlock, myStackPeek(varValueMap.get(baseValue)));
                    }
                } else {
                    break;
                }
            }
        }
        // 遍历支配树后继 DFS
        for (BasicBlock afterBlock : cfg.getDTChildrenList(curBlock)) {
            dfsToRename(afterBlock, varValueMap);
        }
        // 清除新入栈的value
        for (Value value : cntMap.keySet()) {
            int cnt = cntMap.get(value);
            for (; cnt > 0; --cnt) {
                varValueMap.get(value).pop();
            }
        }
    }

    private static Value myStackPeek(Stack<Value> stack) {
        if (stack.size() > 0) {
            return stack.peek();
        } else {
            return IRBuilder.getInstance().newDigit(0);
        }
    }
}
