package frontend.semantics.llvmir.value;

import frontend.semantics.llvmir.type.ArrayType;
import frontend.semantics.llvmir.type.PointerType;

public class GlobalStr extends Value {
    private Module prev;
    private String content;

    public GlobalStr(String name, String content, int length, Module prev) {
        super(
                name, new PointerType(new ArrayType(length))
        );
        this.content = content;
        this.prev = prev;
    }

    @Override
    public String toString() {
        // @.str = private unnamed_addr constant [14 x i8] c"nihao,世界\0A\00"
        return name + " = constant " + ((PointerType) valueType).getTargetType() + " c\"" + content + "\"";
    }
}
