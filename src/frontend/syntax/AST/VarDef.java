package frontend.syntax.AST;

import frontend.semantics.Symbol.SymbolTable;
import frontend.semantics.Symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class VarDef extends Node {
    public VarDef(ArrayList<Node> children) {
        super(SyntaxType.VarDef, children);
    }

    // VarDef → Ident { '[' ConstExp ']' }            -------- b
    //    | Ident { '[' ConstExp ']' } '=' InitVal    -------- k
    @Override
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        String identName = ((LeafNode) children.get(0)).getContent();
        if (st.getSymbol(identName, false) != null) {
            error.append(children.get(0).getLine()).append(" b\n");
            error.append(children.get(children.size() - 1).checkError(st));
        } else {
            ArrayList<Integer> dimension = new ArrayList<>();
            int right = 0;
            for (int i = 1; i < children.size(); ++i) {
                if (children.get(i).getType().equals(SyntaxType.RBRACK)) {
                    right++;
                } else if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                    dimension.add(((ConstExp) children.get(i)).calculate(st).get(0));
                }
            }
            if (right != dimension.size()) {
                error.append(children.get(0).getLine()).append(" k\n");
            }
            if (children.get(children.size() - 1).getType().equals(SyntaxType.InitVal)) {
                error.append(children.get(children.size() - 1).checkError(st));
            }
            st.addSymbol(
                    new VarSymbol(identName, dimension)
            );
        }
        return error.toString();
    }
}
