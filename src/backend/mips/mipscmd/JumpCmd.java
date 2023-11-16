package backend.mips.mipscmd;

import backend.mips.Reg;

public class JumpCmd implements TextCmd {
    public enum JumpCmdOp {
        j, jal, jr;

        @Override
        public String toString() {
            return this.name();
        }
    }

    private JumpCmdOp op;
    private LabelCmd label;
    private Reg reg;

    public JumpCmd(JumpCmdOp op, LabelCmd label) {
        this.op = op;
        this.label = label;
        this.reg = null;
    }

    public JumpCmd(JumpCmdOp op, Reg reg) {
        this.op = op;
        this.reg = reg;
        this.label = null;
    }

    public boolean checkLabel(TextCmd cmd) {
        return op.equals(JumpCmdOp.j) && label.equals(cmd);
    }

    @Override
    public String toString() {
        if (label != null) {
            return op + " " + label.getName();
        } else {
            return op + " " + reg;
        }
    }
}
