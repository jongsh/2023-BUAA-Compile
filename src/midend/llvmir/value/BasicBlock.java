package midend.llvmir.value;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.instr.BRInstr;
import midend.llvmir.value.instr.Instr;
import midend.llvmir.value.instr.RetInstr;

import java.util.ArrayList;
import java.util.HashSet;

public class BasicBlock extends Value {
    private final Function belong;
    private final ArrayList<Instr> instrList;

    // CFG
    private HashSet<BasicBlock> CFGChildren;
    private HashSet<BasicBlock> DFTreeChildren;

    public BasicBlock(String name, Function belong) {
        super(name, new ValueType());
        this.belong = belong;
        this.instrList = new ArrayList<>();
        this.CFGChildren = new HashSet<>();
    }

    public void addInstr(Instr instr) {
        if (instrList.size() > 0 && (instrList.get(instrList.size() - 1) instanceof RetInstr ||
                instrList.get(instrList.size() - 1) instanceof BRInstr)) {
            return;
        }
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
