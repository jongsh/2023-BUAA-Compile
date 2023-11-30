package midend.optimize;

import midend.llvmir.value.Function;
import midend.llvmir.value.Module;

public class Optimizer {

    public static void optimize(Module module) {
        for (Function function: module.getFunctionList()) {
            // 转换成 SSA 形式
            new SSA(function).transToSSA();
        }
        // 死代码删除
        DeadCodeRemover.getInstance().run(module);
    }
}
