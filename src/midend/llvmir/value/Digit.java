package midend.llvmir.value;

import midend.llvmir.type.ValueType;
import midend.llvmir.value.instr.Instr;

import java.util.ArrayList;

public class Digit extends Value {
    public Digit(Integer number, ValueType type) {
        super(number.toString(), type);
    }

    public int getNum() {
        return Integer.parseInt(name);
    }

    public static Digit calculate(Digit digit1, Digit digit2, String type) {
        int num1 = digit1.getNum();
        int num2 = digit2.getNum();
        ValueType valueType = digit1.getValueType();
        switch (type) {
            case "+":
                return new Digit(num1 + num2, valueType);
            case "-":
                return new Digit(num1 - num2, valueType);
            case "*":
                return new Digit(num1 * num2, valueType);
            case "/":
                return new Digit(num1 / num2, valueType);
            case "%":
                return new Digit(num1 % num2, valueType);
            default:
                return null;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Digit) {
            return ((Digit) obj).getNum() == this.getNum() && ((Digit) obj).getValueType().equals(this.valueType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return valueType + " " + name;
    }
}
