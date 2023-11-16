package backend.mips.mipscmd;

import backend.mips.Reg;

public class LaCmd implements TextCmd {

    private Reg targetReg;
    private String addrName;

    public LaCmd(Reg targetReg, String addrName) {
        this.targetReg = targetReg;
        this.addrName = addrName;
    }

    @Override
    public String toString() {
        return "la " + targetReg + ", " + addrName;
    }
}
