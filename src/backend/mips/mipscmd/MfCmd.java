package backend.mips.mipscmd;

import backend.mips.Reg;

public class MfCmd implements TextCmd {
    public enum MfCmdOp {
        mfhi, mflo;

        @Override
        public String toString() {
            return this.name();
        }
    }

    private Reg toReg;
    private MfCmdOp op;

    public MfCmd(MfCmdOp op, Reg toReg) {
        this.op = op;
        this.toReg = toReg;
    }

    @Override
    public String toString() {
        return op + " " + toReg;
    }
}
