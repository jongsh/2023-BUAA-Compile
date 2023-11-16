package midend.llvmir.type;

import java.util.ArrayList;

public class VarType extends ValueType implements Initializable {
    // void int1 int8 int32
    private final int width;
    private final int initial;

    public VarType(int width) {
        this.width = width;
        this.initial = 0;
    }

    public VarType(int width, int initial) {
        this.width = width;
        this.initial = initial;
    }

    public int getWidth() {
        return width;
    }

    @Override
    public int size() {
        return (width == 0) ? 0 : 1;
    }

    @Override
    public String toString() {
        return (width == 0) ? "void" : "i" + width;
    }

    @Override
    public String toLLVMIRString() {
        return this + " " + initial;
    }

    @Override
    public ArrayList<Integer> getInitials() {
        ArrayList<Integer> ret = new ArrayList<>();
        ret.add(initial);
        return ret;
    }
}
