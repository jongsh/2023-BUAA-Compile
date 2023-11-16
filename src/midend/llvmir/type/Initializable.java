package midend.llvmir.type;

import java.util.ArrayList;

public interface Initializable {
    // 生成中间代码初始化值
    String toLLVMIRString();

    // 获取初始值
    ArrayList<Integer> getInitials();

}
