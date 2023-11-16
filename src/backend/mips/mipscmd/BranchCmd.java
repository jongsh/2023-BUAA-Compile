package backend.mips.mipscmd;

import backend.mips.Reg;

public class BranchCmd implements TextCmd {
    public enum BranchCmdOp {
        beq, bne;

        @Override
        public String toString() {
            return this.name();
        }
    }

    private BranchCmdOp op;
    private Reg reg1;
    private Reg reg2;
    private LabelCmd label;

    public BranchCmd(BranchCmdOp op, Reg reg1, Reg reg2, LabelCmd label) {
        this.op = op;
        this.reg1 = reg1;
        this.reg2 = reg2;
        this.label = label;
    }

    @Override
    public String toString() {
        return op + " " + reg1 + ", " + reg2 + ", " + label.getName();
    }
}
