package frontend.semantics.llvmir.value.instr;

public enum InstrType {
    ALLOCA, STORE, LOAD, CALL, GETELEMENTPTR,
    ADD, SUB, MUL, DIV, MOD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
