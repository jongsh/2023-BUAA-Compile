package frontend.semantics.llvmir.type;

public class PointerType extends ValueType {
    private ValueType targetType;

    public PointerType(ValueType targetType) {
        this.targetType = targetType;
    }

    @Override
    public String toString() {
        return targetType + "*";
    }
}
