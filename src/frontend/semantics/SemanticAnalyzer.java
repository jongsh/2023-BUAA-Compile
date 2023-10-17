package frontend.semantics;

import frontend.semantics.Symbol.SymbolTable;
import frontend.syntax.AST.Node;

public class SemanticAnalyzer {
    private final Node ast;

    public SemanticAnalyzer(Node ast) {
        this.ast = ast;
    }

    public String CheckError() {
        //System.out.println(ast.checkError(new SymbolTable("global", null)));
        return ast.checkError(new SymbolTable("global", null));
    }
}
