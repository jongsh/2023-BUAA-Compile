package midend.llvmir.type;

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
    public String toString() {
        return (width == 0) ? "void" : "i" + width;
    }

    @Override
    public String toInitString() {
        return this + " " + initial;
    }
}