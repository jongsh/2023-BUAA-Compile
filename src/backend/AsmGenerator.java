package backend;

import backend.mips.MipsBuilder;
import backend.mips.MipsProcedure;
import midend.llvmir.value.Module;

public class AsmGenerator {
    private static AsmGenerator instance = new AsmGenerator();

    public static AsmGenerator getInstance() {
        return instance;
    }

    public MipsProcedure genMips(Module module) {
        /* TODO 消除phi */
        // 新建 mips 程序
        MipsBuilder.getInstance().reFresh();
        // 中间代码转 mips
        module.toMips();
        // 获得 mips 程序
        return MipsBuilder.getInstance().getProcedure();
    }
}
