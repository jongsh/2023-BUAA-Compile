package frontend.semantics.llvmir.value;

import frontend.semantics.llvmir.type.ValueType;
import frontend.semantics.llvmir.value.instr.InstrType;

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
    public String toString() {
        return valueType + " " + name;
    }
}
