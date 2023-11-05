package frontend.syntax.ast;

import frontend.semantics.llvmir.value.Value;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class InitVal extends Node {
    public InitVal(ArrayList<Node> children) {
        super(SyntaxType.InitVal, children);
    }

    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();
        if (children.get(0).getType().equals(SyntaxType.LBRACE)) {
            for (int i = 1; i < children.size(); i += 2) {
                if (children.get(i) instanceof InitVal) {
                    values.addAll(((InitVal) children.get(i)).calculate());
                }
            }
        } else {
            values.addAll(((Exp) children.get(0)).calculate());
        }
        return values;
    }

    //  InitVal → Exp | '{' [ InitVal { ',' InitVal } ] '}'
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }

    public ArrayList<Value> genIRs() {
        ArrayList<Value> ret = new ArrayList<>();
        if (children.size() == 1) {
            ret.add(children.get(0).genIR());
        } else {
            for (Node child : children) {
                if (child instanceof InitVal) {
                    ret.addAll(((InitVal) child).genIRs());
                }
            }
        }
        return ret;
    }
}
