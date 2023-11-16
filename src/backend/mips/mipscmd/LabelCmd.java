package backend.mips.mipscmd;

public class LabelCmd implements TextCmd {
    public String name;

    public LabelCmd(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LabelCmd) {
            return ((LabelCmd) obj).getName().equals(name);
        }
        return false;
    }

    @Override
    public String toString() {
        return name + ":";
    }
}
