package midend.optimize;

import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Function;
import midend.llvmir.value.Value;
import midend.llvmir.value.instr.AluInstr;
import midend.llvmir.value.instr.GepInstr;
import midend.llvmir.value.instr.IcmpInstr;
import midend.llvmir.value.instr.Instr;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;

public class GVN {
    private static Function function;
    private static CFG cfg;
    private static LinkedHashMap<String, Value> valueMap;

    public static void simplify(Function f, CFG c) {
        function = f;
        cfg = c;
        valueMap = new LinkedHashMap<>();
        BasicBlock entry = function.getBasicBlockList().get(0);
        simplifyDFS(entry);

    }

    private static void simplifyDFS(BasicBlock entry) {
        constantFold(entry);
        HashSet<String> records = new HashSet<>();
        ArrayList<Instr> instrList = entry.getInstrList();
        for (int i = 0; i < instrList.size(); ++i) {
            Instr instr = instrList.get(i);
            if (!(instr instanceof AluInstr || instr instanceof GepInstr || instr instanceof IcmpInstr)) {
                continue;
            } else if (valueMap.containsKey(instr.toGVNString())) {
                instr.modifyToNewValue(valueMap.get(instr.toGVNString()));
                instrList.get(i).deleted();
                instrList.remove(i);
                i--;
            } else {
                records.add(instr.toGVNString());
                valueMap.put(instr.toGVNString(), instr);
            }
        }
        // DFS
        for (BasicBlock next : cfg.getDTChildrenList(entry)) {
            simplifyDFS(next);
        }
        for (String gvnString : records) {
            valueMap.remove(gvnString);
        }
    }

    private static void constantFold(BasicBlock entry) {
        ArrayList<Instr> instrList = entry.getInstrList();
        for (Instr instr : instrList) {
            if (instr instanceof AluInstr) {
                ((AluInstr) instr).simplify();
            }
        }
    }
}
