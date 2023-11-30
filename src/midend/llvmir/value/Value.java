package midend.llvmir.value;

import midend.llvmir.type.ValueType;

import java.util.ArrayList;

public class Value {
    protected String name;
    protected ValueType valueType;
    protected ArrayList<User> userList;

    public Value(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
        this.userList = new ArrayList<>();
    }

    public ValueType getValueType() {
        return valueType;
    }

    public String getName() {
        return name;
    }

    public boolean isUsed() {
        return userList.size() > 0;
    }

    public void addUser(User user) {
        this.userList.add(user);
    }

    public void delete(User user) {
        this.userList.remove(user);
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void modifyToNewValue(Value newValue) {
        for (User user : userList) {
            user.modifyOperand(this, newValue);
        }
    }

    public int size() {
        return valueType.size();
    }

    @Override
    public String toString() {
        return valueType.toString() + " " + name;
    }

    public void toMips() {
    }
}
