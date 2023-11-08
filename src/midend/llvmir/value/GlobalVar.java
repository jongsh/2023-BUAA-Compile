package midend.llvmir.value;

import midend.llvmir.type.Initializable;
import midend.llvmir.type.PointerType;
import midend.llvmir.type.ValueType;

public class GlobalVar extends Value {
    private final Module prev;
    private final boolean isConst;

    public GlobalVar(boolean isConst, String name, ValueType type, Module prev) {
        super(name, new PointerType(type));
        this.isConst = isConst;
        this.prev = prev;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ValueType tempType = ((PointerType)valueType).getTargetType();
        if (isConst) {
            sb.append(name).append(" = dso_local constant ").append(((Initializable) tempType).toInitString());
        } else {
            sb.append(name).append(" = dso_local global ").append(((Initializable) tempType).toInitString());
        }
        return sb.toString();
    }
}
