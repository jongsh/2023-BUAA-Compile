package backend.mips.mipscmd;

import backend.mips.Reg;

public class MoveCmd implements TextCmd {
    private Reg targetReg;
    private Reg sourceReg;

    public MoveCmd(Reg target, Reg source) {
        this.targetReg = target;
        this.sourceReg = source;
    }

    @Override
    public String toString() {
        return "move " + targetReg + " " + sourceReg;
    }
}
