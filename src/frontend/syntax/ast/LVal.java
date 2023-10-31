package frontend.syntax.ast;

import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LVal extends Node {
    public LVal(ArrayList<Node> children) {
        super(SyntaxType.LVal, children);
    }

    // LVal → Ident {'[' Exp ']'} // c k
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();

        String identName = ((LeafNode) children.get(0)).getContent();
        if (SymbolManager.instance().getVarSymbol(identName, true) == null) {                // 判断变量名是否已经定义了
            error.append(children.get(0).getLine()).append(" c\n");
        } else {
            if (children.size() > 1) {
                int left = 0;
                int right = 0;
                for (int i = 1; i < children.size(); ++i) {
                    if (children.get(i).getType().equals(SyntaxType.RBRACK)) {
                        right++;
                    } else if (children.get(i).getType().equals(SyntaxType.LBRACK)) {
                        left++;
                    } else if (children.get(i).getType().equals(SyntaxType.Exp)) {
                        error.append(children.get(i).checkError());
                    }
                }
                if (left != right) {
                    error.append(children.get(0).getLine()).append(" k\n");
                }
            }
        }
        return error.toString();
    }

    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();

        String name = ((LeafNode) children.get(0)).getContent();
        VarSymbol varSymbol = SymbolManager.instance().getVarSymbol(name, true);
        int index = 0;
        if (children.size() > 1) {
            index = ((Exp) children.get(2)).calculate().get(0);
        }
        if (children.size() > 5) {
            index = index * varSymbol.getDimensions().get(1) + ((Exp) children.get(5)).calculate().get(0);
        }
        values.add(varSymbol.getValue(index));
        return values;
    }
}
