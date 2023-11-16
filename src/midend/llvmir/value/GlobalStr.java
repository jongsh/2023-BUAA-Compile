package midend.llvmir.value;

import backend.mips.MipsBuilder;
import midend.llvmir.type.ArrayType;
import midend.llvmir.type.PointerType;

public class GlobalStr extends Value {
    private final Module belong;
    private final String content;

    public GlobalStr(String name, String content, int length, Module belong) {
        super(
                name, new PointerType(new ArrayType(length))
        );
        this.content = content;
        this.belong = belong;
    }

    @Override
    public String toString() {
        String LLVMStr = content.replace("\\n", "\\0A") + "\\00";
        return name + " = constant " + ((PointerType) valueType).getTargetType() + " c\"" + LLVMStr + "\"";
    }

    @Override
    public void toMips() {
        MipsBuilder.getInstance().globalStrToCmd(name.substring(1), content);
    }
}
