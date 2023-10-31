package frontend.syntax.ast;

import frontend.semantics.symbol.FuncSymbol;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class FuncRParams extends Node {
    public FuncRParams(ArrayList<Node> children) {
        super(SyntaxType.FuncRParams, children);
    }

    // FuncRParams → Exp { ',' Exp }
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        if (error.length() == 0) {
            FuncSymbol funcSymbol = SymbolManager.instance().getCurTableFuncSymbol();
            ArrayList<ArrayList<Integer>> params = funcSymbol.getParams();
            if (children.size() / 2 + 1 != params.size()) {
                error.append(children.get(0).getLine()).append(" d\n");
            } else {
                for (int i = 0; i < params.size(); ++i) {
                    int need = params.get(i).size();  //  0 1 2
                    int actual = 0;
                    Node temp;
                    UnaryExp unaryExp = (UnaryExp) children.get(2 * i).searchNode(SyntaxType.UnaryExp);
                    while (unaryExp.size() == 1) {
                        temp = unaryExp.searchNode(SyntaxType.PrimaryExp);
                        if (temp.size() != 1) {
                            unaryExp = (UnaryExp) temp.searchNode(SyntaxType.UnaryExp);
                        } else {
                            break;
                        }
                    }
                    LVal lVal = (LVal) unaryExp.searchNode(SyntaxType.LVal);
                    if (unaryExp.size() == 1 && lVal != null) {
                        String identName = ((LeafNode) lVal.searchNode(SyntaxType.IDENFR)).getContent();
                        VarSymbol varSymbol = SymbolManager.instance().getVarSymbol(identName, true);
                        int dimension = varSymbol.getDimensions().size();
                        actual = (lVal.size() == 1) ? dimension :
                                (lVal.size() == 4) ? dimension - 1 : dimension - 2;
                    } else if (unaryExp.size() == 3 || unaryExp.size() == 4){
                        String identName = ((LeafNode) unaryExp.searchNode(SyntaxType.IDENFR)).getContent();
                        FuncSymbol funcSymbolTemp = SymbolManager.instance().getFuncSymbol(identName);
                        if (funcSymbolTemp.getType().equals("void")) {
                            actual = -1;
                        }
                    }
                    if (need != actual) {
                        error.append(children.get(0).getLine()).append(" e\n");
                        break;
                    }
                }
            }
        }
        return error.toString();
    }
}
