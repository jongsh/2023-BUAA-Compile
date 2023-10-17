package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class MulExp extends Node {
    public MulExp(ArrayList<Node> children) {
        super(SyntaxType.MulExp, children);
    }

    // MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError(st));
        }
        return error.toString();
    }

    public ArrayList<Integer> calculate(SymbolTable st) {
        ArrayList<Integer> values = new ArrayList<>();
        if (children.size() > 1){
            int num1 = ((MulExp) children.get(0)).calculate(st).get(0);
            int num2 = ((UnaryExp) children.get(2)).calculate(st).get(0);
            int value =  (children.get(1).getType().equals(SyntaxType.MULT)) ? num1 * num2 :
                    (children.get(1).getType().equals(SyntaxType.DIV)) ? num1 / num2 : num1 % num2;
            values.add(value);
        } else {
            values.add(((UnaryExp) children.get(0)).calculate(st).get(0));
        }
        return values;
    }
}
