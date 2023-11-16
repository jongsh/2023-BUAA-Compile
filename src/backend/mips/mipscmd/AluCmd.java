package backend.mips.mipscmd;

import backend.mips.Reg;

public class AluCmd implements TextCmd {
    public enum AluCmdOp {
        addiu, slti, xori,
        addu, mul, subu, div, slt;

        @Override
        public String toString() {
            return this.name();
        }
    }

    private final AluCmdOp op;
    private Reg targetReg;
    private Reg sourceReg1;
    private Reg sourceReg2;
    private int immediate;
    private boolean isTypeI;

    public AluCmd(AluCmdOp op, Reg targetReg, Reg sourceReg1, Reg sourceReg2) {
        this.op = op;
        this.isTypeI = false;
        this.targetReg = targetReg;
        this.sourceReg1 = sourceReg1;
        this.sourceReg2 = sourceReg2;
        this.immediate = 0;
    }

    public AluCmd(AluCmdOp op, Reg targetReg, Reg sourceReg, int immediate) {
        this.op = op;
        this.isTypeI = true;
        this.targetReg = targetReg;
        this.sourceReg1 = sourceReg;
        this.sourceReg2 = null;
        this.immediate = immediate;
    }

    @Override
    public String toString() {
        if (op.equals(AluCmdOp.div)) {
            return op + " " + sourceReg1 + ", " + sourceReg2;
        }
        if (isTypeI) {
            return op + " " + targetReg + ", " + sourceReg1 + ", " + immediate;
        } else {
            return op + " " + targetReg + ", " + sourceReg1 + ", " + sourceReg2;
        }
    }
}
