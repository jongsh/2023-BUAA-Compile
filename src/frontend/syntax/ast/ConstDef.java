package frontend.syntax.ast;

import frontend.semantics.llvmir.Value;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class ConstDef extends Node {
    public ConstDef(ArrayList<Node> children) {
        super(SyntaxType.ConstDef, children);
    }


    // ConstDef → Ident { '[' ConstExp ']' } '=' ConstInitVal ---- b k
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();

        String identName = ((LeafNode) children.get(0)).getContent();
        if (SymbolManager.instance().getVarSymbol(identName, false) != null) {
            error.append(children.get(0).getLine()).append(" b\n");
        } else {
            ArrayList<Integer> dimension = new ArrayList<>();
            int right = 0;
            for (int i = 1; i < children.size(); ++i) {
                if (children.get(i).getType().equals(SyntaxType.RBRACK)) {
                    right++;
                }
                if (children.get(i).getType().equals(SyntaxType.ConstExp)) {
                    dimension.add(((ConstExp) children.get(i)).calculate().get(0));
                }
            }
            if (right != dimension.size()) {
                error.append(children.get(0).getLine()).append(" k\n");
            }
            SymbolManager.instance().addVarSymbol(true, identName, dimension,
                    ((ConstInitVal) children.get(children.size() - 1)).calculate()
            );
        }

        return error.toString();
    }
}
