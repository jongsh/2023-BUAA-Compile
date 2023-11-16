package midend.llvmir.value;

import backend.mips.MipsBuilder;
import midend.llvmir.type.ValueType;
import midend.llvmir.value.instr.BrInstr;
import midend.llvmir.value.instr.IcmpInstr;
import midend.llvmir.value.instr.Instr;
import midend.llvmir.value.instr.RetInstr;

import java.util.ArrayList;

public class BasicBlock extends Value {
    private final Function belong;
    private final ArrayList<Instr> instrList;

    public BasicBlock(String name, Function belong) {
        super(name, new ValueType());
        this.belong = belong;
        this.instrList = new ArrayList<>();
    }

    public void addInstr(Instr instr) {
        if (instrList.size() > 0 && (instrList.get(instrList.size() - 1) instanceof RetInstr ||
                instrList.get(instrList.size() - 1) instanceof BrInstr)) {
            return;
        }
        instrList.add(instr);
    }

    public ArrayList<Instr> getInstrList() {
        return instrList;
    }

    public Function getBelong() {
        return belong;
    }

    public ArrayList<BasicBlock> getNextBlocks() {
        Instr instr = instrList.get(instrList.size() - 1);
        ArrayList<BasicBlock> ret = new ArrayList<>();
        if (instr instanceof BrInstr) {
            ret = ((BrInstr) instr).getNextBlocks();
        }
        return ret;
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

    @Override
    public void toMips() {
        MipsBuilder.getInstance().addLabelCmd(name);
        for (Instr instr : instrList) {
            if (!(instr instanceof IcmpInstr)) {
                instr.toMips();
            }
        }
    }
}
