package backend.mips.mipscmd;

import midend.llvmir.type.Initializable;

import java.util.ArrayList;

public class GlobalVarCmd implements DataCmd {

    public String name;
    public ArrayList<Integer> initials;

    public GlobalVarCmd(String varName, ArrayList<Integer> initials) {
        this.name = varName;
        this.initials = initials;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append(": .word ");
        for (int init : initials) {
            sb.append(init).append(",");
        }
//        for (int i = initials.size() - 1; i >= 0; --i) {
//            sb.append(initials.get(i)).append(",");
//        }
        return sb.substring(0, sb.length() - 1);
    }
}
