package midend.optimize;

import midend.llvmir.value.Function;
import midend.llvmir.value.Module;

import java.util.HashMap;

public class Optimizer {
    private static HashMap<Function, CFG> cfgMaps;

    public static void optimize(Module module) {
        cfgMaps = new HashMap<>();
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
