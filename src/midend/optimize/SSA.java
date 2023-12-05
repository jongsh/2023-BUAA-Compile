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

    // ---------------- 插入 phi 指令 转成 SSA 形式 -------------------- //
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
                    ((Instr) toValue).modifyOperand(fromValue, myStackPeek(varValueMap.get(fromValue)), true);
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

    // -------------- 消除 function 中的 phi 指令 ------------------------ //
    public static void eliminatePhi(Function f, CFG c) {
        function = f;
        cfg = c;
        phiToPc(function.getBasicBlockList().get(0));
    }

    // 初始化PC指令
    private static void phiToPc(BasicBlock entry) {
        HashMap<BasicBlock, BasicBlock> oldNewMap = new HashMap<>();
        HashMap<BasicBlock, ArrayList<Instr>> records = new HashMap<>();

        // 初始化 map
        for (BasicBlock prevBlock : cfg.getCFGFatherList(entry)) {
            if (cfg.getCFGChildrenList(prevBlock).size() > 1) {  // 关键边
                BasicBlock newBlock = IRBuilder.getInstance().newBasicBlock();
                oldNewMap.put(prevBlock, newBlock);
                records.put(newBlock, new ArrayList<>());
            } else {
                oldNewMap.put(prevBlock, prevBlock);
                records.put(prevBlock, new ArrayList<>());
            }
        }
        // phi 转 pc
        ArrayList<Instr> instrList = entry.getInstrList();
        BasicBlock dtPre = cfg.getDTFather(entry);  // 支配树的父节点
        for (int i = 0; i < instrList.size(); ++i) {
            Instr instr = instrList.get(i);
            if (instr instanceof PhiInstr) {
                for (BasicBlock prevBlock : cfg.getCFGFatherList(entry)) {
                    Value fromValue = ((PhiInstr) instr).getValueOfBlock(prevBlock);
                    if (fromValue != null) {
                        PCopyInstr pCopyInstr = IRBuilder.getInstance().newPCopyInstr(
                                oldNewMap.get(prevBlock), fromValue, instr);
                        records.get(oldNewMap.get(prevBlock)).add(pCopyInstr);
                    }
                }
                dtPre.getInstrList().add(dtPre.getInstrList().size() - 1, instr); // 转移phi定义点
                instrList.remove(i);
                i--;
            } else {
                break;
            }
        }
        // 插入 pc
        for (BasicBlock prevBlock : cfg.getCFGFatherList(entry)) {
            insertPc(prevBlock, oldNewMap.get(prevBlock), entry, records.get(oldNewMap.get(prevBlock)));
        }
        // dfs
        for (BasicBlock dtNext : cfg.getDTChildrenList(entry)) {
            phiToPc(dtNext);
        }
    }

    private static void insertPc(BasicBlock pre, BasicBlock cur, BasicBlock next, ArrayList<Instr> instrList) {
        if (instrList.size() == 0) {
            return;
        }
        parallelizePc(instrList, cur);
        if (pre.equals(cur)) {  // 不需要插入新的block
            ArrayList<Instr> curInstrList = cur.getInstrList();
            for (Instr instr : instrList) {
                curInstrList.add(curInstrList.size() - 1, instr);
            }
        } else {
            int index = function.getBasicBlockList().indexOf(pre);
            cur.addInstrList(instrList);
            // 修改 pre->next 成 pre->cur
            pre.getInstrList().get(pre.getInstrList().size() - 1).modifyOperand(next, cur, true);
            // 增加 cur->next
            cur.addInstr(IRBuilder.getInstance().newBRInstr(next, cur));
            function.getBasicBlockList().add(index + 1, cur);
        }
    }

    // 并行化 pc
    private static void parallelizePc(ArrayList<Instr> instrList, BasicBlock belong) {
        for (int i = 0; i < instrList.size(); ++i) {
            Value toValue = ((PCopyInstr) instrList.get(i)).getToValue();
            PAllocaInstr pAllocaInstr = null;
            for (int j = i + 1; j < instrList.size(); ++j) {
                if (instrList.get(j) instanceof PCopyInstr && ((PCopyInstr) instrList.get(j)).getFromValue().equals(toValue)) {
                    if (pAllocaInstr == null) {
                        pAllocaInstr = IRBuilder.getInstance().newPAllocaInstr(belong, toValue);
                    }
                    ((PCopyInstr) instrList.get(j)).modifyFromValue(pAllocaInstr);
                }
            }
            if (pAllocaInstr != null) {
                instrList.add(i, pAllocaInstr);
                i++;
            }
        }
    }
}
