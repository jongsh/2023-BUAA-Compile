package midend.optimize;

import midend.llvmir.value.Function;
import midend.llvmir.value.Module;

import java.util.LinkedHashMap;

public class Optimizer {
    private static LinkedHashMap<Function, CFG> cfgMaps;

    public static void optimize(Module module) {
        cfgMaps = new LinkedHashMap<>();
        for (Function function : module.getFunctionList()) {
            CFG cfg = new CFG(function);
            cfgMaps.put(function, cfg);
            // 转换成 SSA 形式
            SSA.transToSSA(function, cfg);
            // GVN 优化
            GVN.simplify(function, cfg);
            // 死代码删除
            DeadCodeRemover.deleteDeadInstr(function);
            // 消除 Phi
            SSA.eliminatePhi(function, cfg);
        }

    }
}
