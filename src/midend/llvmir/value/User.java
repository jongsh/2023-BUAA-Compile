package midend.llvmir.value;

import midend.llvmir.type.ValueType;

import java.util.ArrayList;

public class User extends Value {
    protected ArrayList<Value> operands;

    public User(String name, ValueType type) {
        super(name, type);
        this.operands = new ArrayList<>();
    }

    public void addOperand(Value operand) {
        this.operands.add(operand);
        operand.addUser(this);
    }

    public ArrayList<Value> getOperands() {
        return operands;
    }

    public void modifyOperand(Value oldOperand, Value newOperand, boolean isUpdateOld) {
        int index = operands.indexOf(oldOperand);
        operands.set(index, newOperand);
        newOperand.addUser(this);
        if (isUpdateOld) {
            oldOperand.deleteUser(this);
        }
    }

    public void deleted() {
        for (Value operand : operands) {
            if (operand != null) {
                operand.deleteUser(this);
            }
        }
    }
}
