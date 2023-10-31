package frontend.semantics;

import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.ast.Node;

public class SemanticAnalyzer {
    private final Node ast;

    public SemanticAnalyzer(Node ast) {
        this.ast = ast;
    }

    public String CheckError() {
        SymbolManager.instance().createTable(SymbolTable.TableType.GLOBAL, true);
        return ast.checkError();
    }
}
