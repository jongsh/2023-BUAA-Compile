package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.semantics.Symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class LVal extends Node {
    public LVal(ArrayList<Node> children) {
        super(SyntaxType.LVal, children);
    }

    // LVal → Ident {'[' Exp ']'} // c k
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();

        String identName = ((LeafNode) children.get(0)).getContent();
        if (st.getSymbol(identName, true) == null) {                // 判断函数名是否已经定义了
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
                        error.append(children.get(i).checkError(st));
                    }
                }
                if (left != right) {
                    error.append(children.get(0).getLine()).append(" k\n");
                }
            }
        }
        return error.toString();
    }

    public ArrayList<Integer> calculate(SymbolTable st) {
        ArrayList<Integer> values = new ArrayList<>();

        String name = ((LeafNode) children.get(0)).getContent();
        VarSymbol varSymbol = (VarSymbol) st.getSymbol(name, true);
        int index = 0;
        if (children.size() > 1) {
            index = ((Exp) children.get(2)).calculate(st).get(0);
        }
        if (children.size() > 5) {
            index = index * varSymbol.getDimensions().get(1) + ((Exp) children.get(5)).calculate(st).get(0);
        }
        values.add(varSymbol.getValue(index));
        return values;
    }
}
