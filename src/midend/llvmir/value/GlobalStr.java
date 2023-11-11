package midend.llvmir.value;

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
        return name + " = constant " + ((PointerType) valueType).getTargetType() + " c\"" + content + "\"";
    }
}
