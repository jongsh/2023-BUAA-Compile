package frontend.syntax.AST;

import frontend.semantics.Symbol.Symbol;
import frontend.semantics.Symbol.SymbolTable;
import frontend.semantics.Symbol.VarSymbol;
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
    public String checkError(SymbolTable st) {
        StringBuilder error = new StringBuilder();
        switch (children.get(0).getType()) {
            case LVal:
                String identName = ((LeafNode) children.get(0).searchNode(SyntaxType.IDENFR)).getContent();
                Symbol symbol = st.getSymbol(identName, true);
                if (symbol != null && ((VarSymbol) symbol).isConst()) {
                    error.append(children.get(0).getLine()).append(" h\n");
                }
                for (Node child : children) {
                    error.append(child.checkError(st));
                }
                if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
                    error.append(children.get(children.size() - 1).getLine()).append(" i\n");
                } else if (children.size() == 5) {
                    error.append(children.get(3).getLine()).append(" j\n");
                }
                break;

            case IFTK:
                error.append(children.get(2).checkError(st));
                if (!children.get(3).getType().equals(SyntaxType.RPARENT)) {
                    error.append(children.get(2).getLine()).append(" j\n");
                }
                for (int i = 3; i < children.size(); ++i) {
                    error.append(children.get(i).checkError(st));
                }
                break;

            case FORTK:
                for (int i = 0; i < children.size() - 1; ++i) {
                    error.append(children.get(i).checkError(st));
                }
                SymbolTable nextSt = new SymbolTable("for", st);
                st.addNext(nextSt);
                error.append(children.get(children.size() - 1).checkError(nextSt));
                break;

            case Block:
                SymbolTable next = new SymbolTable("", st);
                st.addNext(next);
                error.append(children.get(0).checkError(next));
                break;

            case BREAKTK:
            case CONTINUETK:
                SymbolTable cur = st;
                while (cur != null) {
                    if (!cur.getName().equals("for")) {
                        cur = cur.getPrev();
                    } else {
                        break;
                    }
                }
                if (cur == null) {
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
                        error.append(children.get(i).checkError(st));
                        count--;
                    }
                    if (count != 0) {
                        error.append(children.get(0).getLine()).append(" l\n");
                    }
                }
                break;

            case RETURNTK:
                for (Node child : children) {
                    error.append(child.checkError(st));
                }
                if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
                    error.append(children.get(children.size() - 1).getLine()).append(" i\n");
                }
                break;

            case Exp:
                error.append(children.get(0).checkError(st));
                if (!children.get(children.size() - 1).getType().equals(SyntaxType.SEMICN)) {
                    error.append(children.get(children.size() - 1).getLine()).append(" i\n");
                }
        }
        return error.toString();
    }
}
