package midend.llvmir.type;

public class PointerType extends ValueType {
    protected ValueType targetType;

    public PointerType(ValueType targetType) {
        this.targetType = targetType;
    }

    public PointerType() {

    }

    public ValueType getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return targetType + "*";
    }

    public static PointerType translate(ArrayType arrayType) {
        return new PointerType(arrayType.getEleType());
    }
}
