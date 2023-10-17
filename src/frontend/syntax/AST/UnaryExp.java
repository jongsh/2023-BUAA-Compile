package frontend.syntax.AST;

import frontend.semantics.Symbol.FuncSymbol;
import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class UnaryExp extends Node {
    public UnaryExp(ArrayList<Node> children) {
        super(SyntaxType.UnaryExp, children);
    }

    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' // c d e j
    //        | UnaryOp UnaryExp
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        if (children.get(0).getType().equals(SyntaxType.IDENFR)) {
            String identName = ((LeafNode) children.get(0)).getContent() + "!";
            if (st.getSymbol(identName, true) == null) {                // 判断函数名是否已经定义了
                error.append(children.get(0).getLine()).append(" c\n");
            } else if (children.size() > 2 && children.get(2).getType().equals(SyntaxType.FuncRParams)) {
                error.append(children.get(2).checkError(new SymbolTable(identName, st)));
            } else if (((FuncSymbol) st.getSymbol(identName, true)).getParams().size() > 0) {
                error.append(children.get(0).getLine()).append(" d\n");
            }
            if (!children.get(children.size() - 1).getType().equals(SyntaxType.RPARENT)) {
                error.append(children.get(children.size() - 1).getLine()).append(" j\n");
            }
        } else {
            for (Node child : children) {
                error.append(child.checkError(st));
            }
        }
        return error.toString();
    }

    public ArrayList<Integer> calculate(SymbolTable st) {
        ArrayList<Integer> values = new ArrayList<>();
        if (children.get(0).getType().equals(SyntaxType.UnaryOp)) {
            values.add(
                    ((UnaryOp) children.get(0)).calculate(st).get(0) * ((UnaryExp) children.get(1)).calculate(st).get(0)
            );
        } else {
            values.addAll(((PrimaryExp) children.get(0)).calculate(st));
        }
        return values;
    }
}
