package backend.mips.mipscmd;

public class GlobalStrCmd implements DataCmd {
    public String name;
    public String content;

    public GlobalStrCmd(String varName, String content) {
        this.name = varName;
        this.content = content;
    }

    @Override
    public String toString() {
        return name + ": .asciiz \"" + content + "\"";
    }
}
