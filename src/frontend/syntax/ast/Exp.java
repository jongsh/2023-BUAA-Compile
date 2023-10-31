package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Exp extends Node {
    public Exp(ArrayList<Node> children) {
        super(SyntaxType.Exp, children);
    }


    //  Exp → AddExp
    @Override
    public String checkError() {
        return children.get(0).checkError();
    }

    public ArrayList<Integer> calculate() {
        return ((AddExp) children.get(0)).calculate();
    }
}
