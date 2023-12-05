package midend.llvmir.value.instr;

import midend.llvmir.IRBuilder;
import midend.llvmir.type.ValueType;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Value;

import java.util.ArrayList;

public class PhiInstr extends Instr {
    private AllocaInstr baseInstr;
    private ArrayList<BasicBlock> fromBlocks;
    private ArrayList<Boolean> records;

    public PhiInstr(String name, ValueType valueType, AllocaInstr baseInstr, ArrayList<BasicBlock> preBlocks,
                    BasicBlock belong) {
        super(name, valueType, InstrType.PHI, belong);
        this.baseInstr = baseInstr;
        this.fromBlocks = preBlocks;
        this.records = new ArrayList<>();
        for (int i = 0; i < preBlocks.size(); ++i) {
            operands.add(IRBuilder.getInstance().newDigit(0));
            records.add(Boolean.FALSE);
        }
    }

    public AllocaInstr getBaseInstr() {
        return this.baseInstr;
    }

    public void fillIn(BasicBlock fromBlock, Value fromValue) {
        int index = fromBlocks.indexOf(fromBlock);
        operands.set(index, fromValue);
        records.set(index, Boolean.TRUE);
        fromValue.addUser(this);
    }

    public Value getValueOfBlock(BasicBlock fromBlock) {
        int index = fromBlocks.indexOf(fromBlock);
        if (index >= 0 && records.get(index)) {
            return operands.get(index);
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" = ").append(instrType).append(" ").append(valueType);
        for (int i = 0; i < fromBlocks.size(); ++i) {
            if (operands.get(i) == null) {
                sb.append(" [0");
            } else {
                sb.append(" [").append(operands.get(i).getName());
            }
            sb.append(", %").append(fromBlocks.get(i).getName()).append("],");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
