package backend;

import backend.mips.MipsBuilder;
import backend.mips.MipsProcedure;
import midend.llvmir.value.Function;
import midend.llvmir.value.Module;
import midend.optimize.CFG;
import midend.optimize.DeadCodeRemover;
import midend.optimize.GVN;
import midend.optimize.SSA;

public class AsmGenerator {
    private static AsmGenerator instance = new AsmGenerator();

    public static AsmGenerator getInstance() {
        return instance;
    }

    public MipsProcedure genMips(Module module) {
        // 消除 Phi
        for (Function function : module.getFunctionList()) {
            CFG cfg = new CFG(function);
            SSA.eliminatePhi(function, cfg);
        }
        // 新建 mips 程序
        MipsBuilder.getInstance().reFresh();
        // 中间代码转 mips
        module.toMips();
        // 获得 mips 程序
        return MipsBuilder.getInstance().getProcedure();
    }
}
