package midend.optimize;

import midend.llvmir.value.Function;
import midend.llvmir.value.Module;

public class Optimizer {

    public static void optimize(Module module) {
        for (Function function: module.getFunctionList()) {
            System.out.println(new CFG(function));
        }
    }
}
