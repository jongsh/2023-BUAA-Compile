package midend.llvmir.value;

import backend.mips.MipsBuilder;
import midend.llvmir.type.Initializable;
import midend.llvmir.type.PointerType;
import midend.llvmir.type.ValueType;

public class GlobalVar extends Value {
    private final Module belong;
    private final boolean isConst;

    public GlobalVar(boolean isConst, String name, ValueType type, Module belong) {
        super(name, new PointerType(type));
        this.isConst = isConst;
        this.belong = belong;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        ValueType tempType = ((PointerType) valueType).getTargetType();
        if (isConst) {
            sb.append(name).append(" = dso_local constant ").append(((Initializable) tempType).toLLVMIRString());
        } else {
            sb.append(name).append(" = dso_local global ").append(((Initializable) tempType).toLLVMIRString());
        }
        return sb.toString();
    }

    @Override
    public void toMips() {
        Initializable init = (Initializable) ((PointerType) valueType).getTargetType();
        MipsBuilder.getInstance().globalVarToCmd(name.substring(1), init.getInitials());
    }
}
