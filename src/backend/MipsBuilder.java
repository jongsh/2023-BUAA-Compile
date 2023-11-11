package backend;

public class MipsBuilder {
    private static MipsBuilder instance = new MipsBuilder();

    public static MipsBuilder getInstance() {
        return instance;
    }
}
