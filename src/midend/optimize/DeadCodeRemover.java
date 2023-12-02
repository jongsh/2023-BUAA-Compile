package midend.optimize;

import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Function;
import midend.llvmir.value.Module;
import midend.llvmir.value.Value;
import midend.llvmir.value.instr.Instr;

import java.util.ArrayList;
import java.util.HashSet;

public class DeadCodeRemover {

    public static void deleteDeadInstr(Function function) {
        HashSet<Instr> deadInstrSet = new HashSet<>();
        HashSet<Instr> records = new HashSet<>();
        for (BasicBlock block : function.getBasicBlockList()) {
            ArrayList<Instr> instrList = block.getInstrList();
            for (int i = 0; i < instrList.size(); ++i) {
                if (deleteDeadInstrDFS(instrList.get(i), deadInstrSet, records)) {
                    instrList.get(i).deleted();
                    instrList.remove(i);
                    i--;
                }
            }
        }
    }

    // 递归判断是否可以删除指令
    private static boolean deleteDeadInstrDFS(Instr instr, HashSet<Instr> deadInstrSet, HashSet<Instr> records) {
        if (!instr.canBeDelete()) {
            // 不可删除
            return false;
        } else if (deadInstrSet.contains(instr)) {
            // 已经被判定成死代码
            return true;
        } else if (!instr.isUsed()) {
            // 不被使用->死代码
            deadInstrSet.add(instr);
            return true;
        } else if (records.contains(instr)) {
            // 正在记录
            return false;
        } else {
            // 根据 def-use 链递归判断
            records.add(instr);
            boolean isDead = true;
            for (Value user : instr.getUserList()) {
                isDead &= deleteDeadInstrDFS((Instr) user, deadInstrSet, records);
            }
            if (isDead) {
                deadInstrSet.add(instr);
            }
            return isDead;
        }
    }
}
