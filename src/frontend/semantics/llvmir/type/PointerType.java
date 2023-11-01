package frontend.semantics.llvmir.type;

public class PointerType {
    private ValueType target;

    public PointerType(ValueType target) {
        this.target = target;
    }
}
