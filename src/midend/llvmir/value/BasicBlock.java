package midend.llvmir.value;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.instr.Instr;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final Function prev;
    private final ArrayList<Instr> instrList;

    public BasicBlock(String name, Function prev) {
        super(name, new ValueType());
        this.prev = prev;
        this.instrList = new ArrayList<>();
    }

    public void addInstr(Instr instr) {
        instrList.add(instr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");

        sb.append("\n").append(name).append(":\n");
        for (Instr instr : instrList) {
            sb.append("\t").append(instr).append("\n");
        }

        return sb.toString();
    }
}
