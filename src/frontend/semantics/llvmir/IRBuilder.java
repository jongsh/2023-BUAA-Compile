package frontend.semantics.llvmir;

public class IRBuilder {

    private final static IRBuilder instance = new IRBuilder();
    private IRBuilder() {

    }

    public static IRBuilder getInstance() {
        return instance;
    }
}
