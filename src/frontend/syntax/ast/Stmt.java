package frontend.syntax.ast;

import frontend.semantics.symbol.Symbol;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class Stmt extends Node {
    public Stmt(ArrayList<Node> children) {
        super(SyntaxType.Stmt, children);
    }

    // Stmt → LVal '=' Exp ';'
    //    | LVal '=' 'getint''('')'';'
    //    | [Exp] ';'
    //    | Block
    //    | 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
    //    | 'for' '('[ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    //    | 'break' ';' | 'continue' ';'
    //    | 'return' [Exp] ';'
    //    | 'printf''('FormatString{,Exp}')'';'
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        switch (children.get(0).getType()) {
            case LVal:
                String identName = ((LeafNode) children.get(0).searchNode(SyntaxType.IDENFR)).getContent();
                VarSymbol symbol = SymbolManager.instance().getVarSymbol(identName, true);
                if (symbol != null && symbol.isConst()) {
                    error.append(children.get(0).getLine()).append(" h\n");
                }
                for (Node child : children) {
                    error.append(child.checkError());
                }
                if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
                    error.append(children.get(children.size() - 1).getLine()).append(" i\n");
                } else if (children.size() == 5) {
                    error.append(children.get(3).getLine()).append(" j\n");
                }
                break;

            case IFTK:
                error.append(children.get(2).checkError());
                if (!children.get(3).getType().equals(SyntaxType.RPARENT)) {
                    error.append(children.get(2).getLine()).append(" j\n");
                }
                for (int i = 3; i < children.size(); ++i) {
                    error.append(children.get(i).checkError());
                }
                break;

            case FORTK:
                for (int i = 0; i < children.size() - 1; ++i) {
                    error.append(children.get(i).checkError());
                }
                SymbolManager.instance().createTable(SymbolTable.TableType.FOR_BLOCK, true);
                error.append(children.get(children.size() - 1).checkError());
                SymbolManager.instance().tracebackTable();
                break;

            case Block:
                SymbolManager.instance().createTable(SymbolTable.TableType.BLOCK, true);
                error.append(children.get(0).checkError());
                SymbolManager.instance().tracebackTable();
                break;

            case BREAKTK:
            case CONTINUETK:
                if (!SymbolManager.instance().isInTable(SymbolTable.TableType.FOR_BLOCK)) {
                    error.append(children.get(0).getLine()).append(" m\n");
                } else if (children.size() != 2) {
                    error.append(children.get(0).getLine()).append(" i\n");
                }
                break;

            case PRINTFTK:
                if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
                    error.append(children.get(children.size() - 1).getLine()).append(" i\n");
                } else if (!children.get(children.size() - 2).getType().equals(SyntaxType.RPARENT)) {
                    error.append(children.get(children.size() - 2).getLine()).append(" j\n");
                } else {
                    String str = ((LeafNode) children.get(2)).getContent();
                    int count = 0;
                    for (int i = 1; i < str.length() - 1; ++i) {
                        if (str.charAt(i) == '%') {
                            if (i + 1 < str.length() && str.charAt(i + 1) == 'd') {
                                count++;
                            } else {
                                error.append(children.get(2).getLine()).append(" a\n");
                                return error.toString();
                            }
                        } else if (str.charAt(i) == '\\') {
                            if (i + 1 >= str.length() || str.charAt(i + 1) != 'n') {
                                error.append(children.get(2).getLine()).append(" a\n");
                                return error.toString();
                            }
                        } else if (str.charAt(i) == '&' || str.charAt(i) == '"' || str.charAt(i) == '\''
                                || str.charAt(i) == '#' || str.charAt(i) == '$') {
                            error.append(children.get(2).getLine()).append(" a\n");
                            return error.toString();
                        }
                    }
                    for (int i = 4; i < children.size() - 2; i += 2) {
                        error.append(children.get(i).checkError());
                        count--;
                    }
                    if (count != 0) {
                        error.append(children.get(0).getLine()).append(" l\n");
                    }
                }
                break;

            case RETURNTK:
                for (Node child : children) {
                    error.append(child.checkError());
                }
                if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
                    error.append(children.get(children.size() - 1).getLine()).append(" i\n");
                }
                break;

            case Exp:
                error.append(children.get(0).checkError());
                if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
                    error.append(children.get(children.size() - 1).getLine()).append(" i\n");
                }
        }
        return error.toString();
    }
}
