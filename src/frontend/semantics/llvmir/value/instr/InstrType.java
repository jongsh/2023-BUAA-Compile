package frontend.semantics.llvmir.value.instr;

public enum InstrType {
    ALLOCA, STORE, LOAD, CALL, GETELEMENTPTR, RET, BR, ZEXT,
    ADD, SUB, MUL, DIV, MOD, ICMP;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
