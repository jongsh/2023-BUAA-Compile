package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstExp extends Node {
    public ConstExp(ArrayList<Node> children) {
        super(SyntaxType.ConstExp, children);
    }

    @Override
    public String checkError(SymbolTable st) {
        return children.get(0).checkError(st);
    }

    public ArrayList<Integer> calculate(SymbolTable st) {
        return ((AddExp) children.get(0)).calculate(st);
    }
}
