package backend.mips.mipscmd;

public class NoteCmd implements TextCmd {
    private String content;

    public NoteCmd(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
