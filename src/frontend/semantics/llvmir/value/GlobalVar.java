package frontend.semantics.llvmir.value;

import frontend.semantics.llvmir.type.Initializable;
import frontend.semantics.llvmir.type.ValueType;

public class GlobalVar extends Value {
    private final Module prev;
    private final boolean isConst;

    public GlobalVar(boolean isConst, String name, ValueType type, Module prev) {
        super(name, type);
        this.isConst = isConst;
        this.prev = prev;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (isConst) {
            sb.append(name).append(" = dso_local constant ").append(((Initializable) type).toInitString());
        } else {
            sb.append(name).append(" = dso_local global ").append(((Initializable) type).toInitString());
        }
        return sb.toString();
    }
}
