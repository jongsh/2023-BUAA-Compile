package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.Value;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Number extends Node {
    public Number(ArrayList<Node> children) {
        super(SyntaxType.Number, children);
    }

    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();
        values.add(Integer.parseInt(((LeafNode) children.get(0)).getContent()));
        return values;
    }

    @Override
    public Value genIR() {
        return IRBuilder.getInstance().newDigit(
                Integer.parseInt(((LeafNode) children.get(0)).getContent())
        );
    }
}
