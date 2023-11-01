package frontend.semantics.llvmir.value;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.instr.Instr;

import java.util.ArrayList;

public class Function extends Value {
    private final Module prev;
    private ArrayList<Param> paramList;
    private ArrayList<Instr> instrList;

    public Function(String name, ValueType type, Module prev) {
        super(name, type);
        this.prev = prev;
        this.instrList = new ArrayList<>();
        this.paramList = new ArrayList<>();
    }

    public void addInstr(Instr instr) {
        this.instrList.add(instr);
    }

    public void addParam(Param param) {
        this.paramList.add(param);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("define dso_local ").append(type).append(" ").append(name);
        sb.append("(, ");
        for (Param param : paramList) {
            sb.append(param).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(") {\n");
        for (Instr instr : instrList) {
            sb.append(instr);
        }
        sb.append("}");
        return sb.toString();
    }
}
