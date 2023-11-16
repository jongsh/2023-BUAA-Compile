package backend.mips.mipscmd;

import backend.mips.Reg;

public class MemCmd implements TextCmd {
    public enum MemCmdOp {
        lw, sw;

        @Override
        public String toString() {
            return this.name();
        }
    }

    private MemCmdOp op;
    private Reg target;
    private Reg basic;
    private int offset;

    public MemCmd(MemCmdOp op, Reg target, Reg basic, int offset) {
        this.op = op;
        this.target = target;
        this.basic = basic;
        this.offset = offset;
    }

    @Override
    public String toString() {
        return op + " " + target + ", " + offset + "(" + basic + ")";
    }
}
