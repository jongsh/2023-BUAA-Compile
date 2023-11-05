package frontend.syntax;

import frontend.lexer.LexType;
import frontend.lexer.Lexer;
import frontend.syntax.ast.*;
import frontend.syntax.ast.Number;

import java.util.ArrayList;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    private void error() {
        System.out.println("something is wrong at line " + lexer.getLineNumber() + ", token is " + lexer.getToken());
    }

    // CompUnit -> {Dec]} {FuncDef} MainFuncDef
    public CompUnit parseCompUnit() {
        lexer.next();
        ArrayList<Node> children = new ArrayList<>();
        ArrayList<LexType> latter;
        while (!(latter = lexer.foresee(2)).get(0).equals(LexType.MAINTK)) {
            if (lexer.getLexType().equals(LexType.CONSTTK)) {
                children.add(parseConstDecl());
            } else if (lexer.getLexType().equals(LexType.VOIDTK)) {
                children.add(parseFuncDef());
            } else if (latter.get(0).equals(LexType.IDENFR) && latter.get(1).equals(LexType.LPARENT)) {
                children.add(parseFuncDef());
            } else {
                children.add(parseVarDecl());
            }
        }
        children.add(parseMainFuncDef());
        return new CompUnit(children);
    }

    // ConstDecl -> 'const' 'int' ConstDef {',' ConstDef} ';'
    private ConstDecl parseConstDecl() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(new LeafNode(SyntaxType.CONSTTK, "const", lexer.getLineNumber()));
        lexer.next();
        children.add(new LeafNode(SyntaxType.INTTK, "int", lexer.getLineNumber()));
        lexer.next();
        children.add(parseConstDef());
        while (lexer.getLexType().equals(LexType.COMMA)) {
            children.add(new LeafNode(SyntaxType.COMMA, ",", lexer.getLineNumber()));
            lexer.next();
            children.add(parseConstDef());
        }
        if (lexer.getLexType().equals(LexType.SEMICN)) {
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
        }
        return new ConstDecl(children);
    }

    // ConstDef -> Ident { '[' ConstExp ']' } '=' ConstInitVal
    private ConstDef parseConstDef() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.IDENFR)) {
            children.add(new LeafNode(SyntaxType.IDENFR, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
        } else {
            error();
        }

        while (lexer.getLexType().equals(LexType.LBRACK)) {
            children.add(new LeafNode(SyntaxType.LBRACK, "[", lexer.getLineNumber()));
            lexer.next();
            children.add(parseConstExp());
            if (lexer.getLexType().equals(LexType.RBRACK)) {
                children.add(new LeafNode(SyntaxType.RBRACK, "]", lexer.getLineNumber()));
                lexer.next();
            }
        }
        if (lexer.getLexType().equals(LexType.ASSIGN)) {
            children.add(new LeafNode(SyntaxType.ASSIGN, "=", lexer.getLineNumber()));
            lexer.next();
            children.add(parseConstInitVal());
            return new ConstDef(children);
        } else {
            error();
            return null;
        }
    }

    // ConstInitVal -> ConstExp | '{' [ConstInitVal {',' ConstInitVal}] '}'
    private ConstInitVal parseConstInitVal() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.LBRACE)) {
            children.add(new LeafNode(SyntaxType.LBRACE, "{", lexer.getLineNumber()));
            lexer.next();
            children.add(parseConstInitVal());
            while (lexer.getLexType().equals(LexType.COMMA)) {
                children.add(new LeafNode(SyntaxType.COMMA, ",", lexer.getLineNumber()));
                lexer.next();
                children.add(parseConstInitVal());
            }
            if (lexer.getLexType().equals(LexType.RBRACE)) {
                children.add(new LeafNode(SyntaxType.RBRACE, "}", lexer.getLineNumber()));
                lexer.next();
                return new ConstInitVal(children);
            } else {
                error();
                return null;
            }
        } else {
            children.add(parseConstExp());
            return new ConstInitVal(children);
        }
    }

    // VarDecl -> BType VarDef { ',' VarDef } ';'
    private VarDecl parseVarDecl() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(new LeafNode(SyntaxType.INTTK, "int", lexer.getLineNumber()));
        lexer.next();
        children.add(parseVarDef());
        while (lexer.getLexType().equals(LexType.COMMA)) {
            children.add(new LeafNode(SyntaxType.COMMA, ",", lexer.getLineNumber()));
            lexer.next();
            children.add(parseVarDef());
        }
        if (lexer.getLexType().equals(LexType.SEMICN)) {
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
        }
        return new VarDecl(children);
    }

    // VarDef -> Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
    private VarDef parseVarDef() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.IDENFR)) {
            children.add(new LeafNode(SyntaxType.IDENFR, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
        } else {
            error();
        }
        while (lexer.getLexType().equals(LexType.LBRACK)) {
            children.add(new LeafNode(SyntaxType.LBRACK, "[", lexer.getLineNumber()));
            lexer.next();
            children.add(parseConstExp());
            if (lexer.getLexType().equals(LexType.RBRACK)) {
                children.add(new LeafNode(SyntaxType.RBRACK, "]", lexer.getLineNumber()));
                lexer.next();
            }
        }
        if (lexer.getLexType().equals(LexType.ASSIGN)) {
            children.add(new LeafNode(SyntaxType.ASSIGN, "=", lexer.getLineNumber()));
            lexer.next();
            children.add(parseInitVal());
        }
        return new VarDef(children);
    }

    // InitVal -> Exp | '{' [ InitVal { ',' InitVal} ] '}'
    private InitVal parseInitVal() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.LBRACE)) {
            children.add(new LeafNode(SyntaxType.LBRACE, "{", lexer.getLineNumber()));
            lexer.next();
            children.add(parseInitVal());
            while (lexer.getLexType().equals(LexType.COMMA)) {
                children.add(new LeafNode(SyntaxType.COMMA, ",", lexer.getLineNumber()));
                lexer.next();
                children.add(parseInitVal());
            }
            if (lexer.getLexType().equals(LexType.RBRACE)) {
                children.add(new LeafNode(SyntaxType.RBRACE, "}", lexer.getLineNumber()));
                lexer.next();
                return new InitVal(children);
            } else {
                error();
                return null;
            }
        } else {
            children.add(parseExp());
            return new InitVal(children);
        }
    }

    // FuncDef -> FuncType Ident '(' [FuncFParams] ')' Block
    private FuncDef parseFuncDef() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseFuncType());
        if (lexer.getLexType().equals(LexType.IDENFR)) {
            children.add(new LeafNode(SyntaxType.IDENFR, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
        } else {
            error();
            return null;
        }
        children.add(new LeafNode(SyntaxType.LPARENT, "(", lexer.getLineNumber()));
        lexer.next();
        if (!lexer.getLexType().equals(LexType.RPARENT) && !lexer.getLexType().equals(LexType.LBRACE)) {
            children.add(parseFuncFParams());
        }
        if (lexer.getLexType().equals(LexType.RPARENT)) {
            children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
            lexer.next();
        }
        children.add(parseBlock());
        return new FuncDef(children);
    }

    // MainFuncDef -> 'int' 'main' '(' ')' Block
    private MainFuncDef parseMainFuncDef() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.INTTK)) {
            children.add(new LeafNode(SyntaxType.INTTK, "int", lexer.getLineNumber()));
            lexer.next();
        }
        if (lexer.getLexType().equals(LexType.MAINTK)) {
            children.add(new LeafNode(SyntaxType.MAINTK, "main", lexer.getLineNumber()));
            lexer.next();
        }
        if (lexer.getLexType().equals(LexType.LPARENT)) {
            children.add(new LeafNode(SyntaxType.LPARENT, "(", lexer.getLineNumber()));
            lexer.next();
        }
        if (lexer.getLexType().equals(LexType.RPARENT)) {
            children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
            lexer.next();
        }
        children.add(parseBlock());
        return new MainFuncDef(children);
    }

    // FuncType -> 'void' | 'int'
    private FuncType parseFuncType() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(lexer.getLexType().equals(LexType.VOIDTK) ?
                new LeafNode(SyntaxType.VOIDTK, "void", lexer.getLineNumber()) :
                new LeafNode(SyntaxType.INTTK, "int", lexer.getLineNumber()));
        lexer.next();
        return new FuncType(children);
    }

    // FunFParams -> FuncFParam { ',' FuncFParam }
    private FuncFParams parseFuncFParams() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseFuncFParam());
        while (lexer.getLexType().equals(LexType.COMMA)) {
            children.add(new LeafNode(SyntaxType.COMMA, ",", lexer.getLineNumber()));
            lexer.next();
            children.add(parseFuncFParam());
        }
        return new FuncFParams(children);
    }

    // FuncFParam -> BType Ident [ '[' ']' { '[' ConstExp ']'} ]
    private FuncFParam parseFuncFParam() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.INTTK)) {
            children.add(new LeafNode(SyntaxType.INTTK, "int", lexer.getLineNumber()));
            lexer.next();
        }
        if (lexer.getLexType().equals(LexType.IDENFR)) {
            children.add(new LeafNode(SyntaxType.IDENFR, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
        }
        if (!lexer.getLexType().equals(LexType.LBRACK)) {
            return new FuncFParam(children);
        }
        children.add(new LeafNode(SyntaxType.LBRACK, "[", lexer.getLineNumber()));
        lexer.next();
        if (lexer.getLexType().equals(LexType.RBRACK)) {
            children.add(new LeafNode(SyntaxType.RBRACK, "]", lexer.getLineNumber()));
            lexer.next();
        }

        while (lexer.getLexType().equals(LexType.LBRACK)) {
            children.add(new LeafNode(SyntaxType.LBRACK, "[", lexer.getLineNumber()));
            lexer.next();
            children.add(parseConstExp());
            if (lexer.getLexType().equals(LexType.RBRACK)) {
                children.add(new LeafNode(SyntaxType.RBRACK, "]", lexer.getLineNumber()));
                lexer.next();
            }
        }
        return new FuncFParam(children);
    }

    // Block -> '{' { Decl | Stmt } '}'
    private Block parseBlock() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.LBRACE)) {
            children.add(new LeafNode(SyntaxType.LBRACE, "{", lexer.getLineNumber()));
            lexer.next();
        }
        while (!lexer.getLexType().equals(LexType.RBRACE)) {
            if (lexer.getLexType().equals(LexType.CONSTTK)) {
                children.add(parseConstDecl());
            } else if (lexer.getLexType().equals(LexType.INTTK)) {
                children.add(parseVarDecl());
            } else {
                children.add(parseStmt());
            }
        }
        children.add(new LeafNode(SyntaxType.RBRACE, "}", lexer.getLineNumber()));
        lexer.next();
        return new Block(children);
    }

    private Stmt parseStmt() {
        ArrayList<Node> children = new ArrayList<>();

        if (lexer.getLexType().equals(LexType.IFTK)) {
            return parseStmtIf();
        } else if (lexer.getLexType().equals(LexType.FORTK)) {
            return parseStmtFor();
        } else if (lexer.getLexType().equals(LexType.LBRACE)) {
            children.add(parseBlock());
            return new Stmt(children);
        } else if (lexer.getLexType().equals(LexType.BREAKTK)) {
            children.add(new LeafNode(SyntaxType.BREAKTK, "break", lexer.getLineNumber()));
            lexer.next();
        } else if (lexer.getLexType().equals(LexType.CONTINUETK)) {
            children.add(new LeafNode(SyntaxType.CONTINUETK, "continue", lexer.getLineNumber()));
            lexer.next();
        } else if (lexer.getLexType().equals(LexType.RETURNTK)) {
            children.add(new LeafNode(SyntaxType.RETURNTK, "return", lexer.getLineNumber()));
            lexer.next();
            LexType temp = lexer.getLexType();
            if (!temp.equals(LexType.SEMICN)) {
                if (temp.equals(LexType.CONSTTK) || temp.equals(LexType.INTTK) || temp.equals(LexType.RBRACE)
                        || temp.equals(LexType.LBRACE) || temp.equals(LexType.IFTK) || temp.equals(LexType.FORTK)
                        || temp.equals(LexType.BREAKTK) || temp.equals(LexType.RETURNTK) || temp.equals(LexType.PRINTFTK)) {
                    return new Stmt(children);
                }
                lexer.store();
                Exp exp = parseExp();
                if (lexer.getLexType().equals(LexType.ASSIGN)) {
                    lexer.reStore();
                } else {
                    children.add(exp);
                }
            }
        } else if (lexer.getLexType().equals(LexType.PRINTFTK)) {
            return parseStmtPrint();
        } else if (lexer.getLexType().equals(LexType.SEMICN)) {
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
            return new Stmt(children);
        } else {
            Exp temp = parseExp();
            if (lexer.getLexType().equals(LexType.ASSIGN)) {
                LVal lVal = (LVal) temp.searchNode(SyntaxType.LVal);
                lVal.setLeft(true);
                children.add(lVal);
                children.add(new LeafNode(SyntaxType.ASSIGN, "=", lexer.getLineNumber()));
                lexer.next();
                if (lexer.getLexType().equals(LexType.GETINTTK)) {
                    children.add(new LeafNode(SyntaxType.GETINTTK, "getint", lexer.getLineNumber()));
                    lexer.next();
                    children.add(new LeafNode(SyntaxType.LPARENT, "(", lexer.getLineNumber()));
                    lexer.next();
                    if (lexer.getLexType().equals(LexType.RPARENT)) {
                        children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
                        lexer.next();
                    }
                } else {
                    children.add(parseExp());
                }
            } else {
                children.add(temp);
            }
        }

        if (lexer.getLexType().equals(LexType.SEMICN)) {
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
        }

        return new Stmt(children);
    }

    // Stmt -> 'if' '(' Cond ')' Stmt ['else' Stmt]
    private Stmt parseStmtIf() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.IFTK)) {
            children.add(new LeafNode(SyntaxType.IFTK, "if", lexer.getLineNumber()));
            lexer.next();
        }
        if (lexer.getLexType().equals(LexType.LPARENT)) {
            children.add(new LeafNode(SyntaxType.LPARENT, "(", lexer.getLineNumber()));
            lexer.next();
        }
        children.add(parseCond());
        if (lexer.getLexType().equals(LexType.RPARENT)) {
            children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
            lexer.next();
        }
        children.add(parseStmt());
        if (lexer.getLexType().equals(LexType.ELSETK)) {
            children.add(new LeafNode(SyntaxType.ELSETK, "else", lexer.getLineNumber()));
            lexer.next();
            children.add(parseStmt());
        }
        return new Stmt(children);

    }

    // Stmt -> 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt
    private Stmt parseStmtFor() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.FORTK)) {
            children.add(new LeafNode(SyntaxType.FORTK, "for", lexer.getLineNumber()));
            lexer.next();
        } else {
            error();
            return null;
        }
        children.add(new LeafNode(SyntaxType.LPARENT, "(", lexer.getLineNumber()));
        lexer.next();
        if (lexer.getLexType().equals(LexType.SEMICN)) {
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
        } else {
            children.add(parseForStmt());
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
        }
        if (lexer.getLexType().equals(LexType.SEMICN)) {
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
        } else {
            children.add(parseCond());
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
        }
        if (lexer.getLexType().equals(LexType.RPARENT)) {
            children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
            lexer.next();
        } else {
            children.add(parseForStmt());
            children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
            lexer.next();
        }
        children.add(parseStmt());
        return new Stmt(children);
    }

    // 'printf' '(' FormatString {',' Exp} ')' ';'
    private Stmt parseStmtPrint() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(new LeafNode(SyntaxType.PRINTFTK, "printf", lexer.getLineNumber()));
        lexer.next();
        children.add(new LeafNode(SyntaxType.LPARENT, "(", lexer.getLineNumber()));
        lexer.next();
        children.add(new LeafNode(SyntaxType.STRCON, lexer.getToken(), lexer.getLineNumber()));
        lexer.next();
        while (lexer.getLexType().equals(LexType.COMMA)) {
            children.add(new LeafNode(SyntaxType.COMMA, ",", lexer.getLineNumber()));
            lexer.next();
            children.add(parseExp());
        }
        if (lexer.getLexType().equals(LexType.RPARENT)) {
            children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
            lexer.next();
        }
        if (lexer.getLexType().equals(LexType.SEMICN)) {
            children.add(new LeafNode(SyntaxType.SEMICN, ";", lexer.getLineNumber()));
            lexer.next();
        }
        return new Stmt(children);
    }

    // ForStmt -> LVal '=' Exp
    private ForStmt parseForStmt() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseLVal());
        if (lexer.getLexType().equals(LexType.ASSIGN)) {
            children.add(new LeafNode(SyntaxType.ASSIGN, "=", lexer.getLineNumber()));
            lexer.next();
        }
        children.add(parseExp());
        return new ForStmt(children);
    }

    // Exp -> AddExp
    private Exp parseExp() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseAddExp());
        return new Exp(children);
    }

    // Cond -> LOrExp
    private Cond parseCond() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseLOrExp());
        return new Cond(children);
    }

    // LVal -> Ident { '[' Exp ']'}
    private LVal parseLVal() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.IDENFR)) {
            children.add(new LeafNode(SyntaxType.IDENFR, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
        }
        while (lexer.getLexType().equals(LexType.LBRACK)) {
            children.add(new LeafNode(SyntaxType.LBRACK, "[", lexer.getLineNumber()));
            lexer.next();
            children.add(parseExp());
            if (lexer.getLexType().equals(LexType.RBRACK)) {
                children.add(new LeafNode(SyntaxType.RBRACK, "]", lexer.getLineNumber()));
                lexer.next();
            }
        }
        return new LVal(children);
    }

    // PrimaryExp -> '(' Exp ')' | LVal | Number
    private PrimaryExp parsePrimaryExp() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.LPARENT)) {
            children.add(new LeafNode(SyntaxType.LPARENT, "(", lexer.getLineNumber()));
            lexer.next();
            children.add(parseExp());
            if (lexer.getLexType().equals(LexType.RPARENT)) {
                children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
                lexer.next();
            }
        } else if (lexer.getLexType().equals(LexType.INTCON)) {
            children.add(parseNumber());
        } else {
            children.add(parseLVal());
        }
        return new PrimaryExp(children);
    }

    // Number -> IntConst
    private Number parseNumber() {
        ArrayList<Node> children = new ArrayList<>();
        if (lexer.getLexType().equals(LexType.INTCON)) {
            children.add(new LeafNode(SyntaxType.INTCON, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
            return new Number(children);
        } else {
            error();
            return null;
        }
    }

    // UnaryExp -> Primary | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
    private UnaryExp parseUnaryExp() {
        ArrayList<Node> children = new ArrayList<>();
        ArrayList<LexType> lexTypes = lexer.foresee(1);
        if (lexTypes.get(0).equals(LexType.LPARENT) && lexer.getLexType().equals(LexType.IDENFR)) {
            children.add(new LeafNode(SyntaxType.IDENFR, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
            children.add(new LeafNode(SyntaxType.LPARENT, "(", lexer.getLineNumber()));
            lexer.next();
            if (lexer.getLexType().equals(LexType.IDENFR) || lexer.getLexType().equals(LexType.LPARENT)
                    || lexer.getLexType().equals(LexType.INTCON) || lexer.getLexType().equals(LexType.PLUS)
                    || lexer.getLexType().equals(LexType.MINU)) {
                children.add(parseFuncRParams());
            }
            if (lexer.getLexType().equals(LexType.RPARENT)) {
                children.add(new LeafNode(SyntaxType.RPARENT, ")", lexer.getLineNumber()));
                lexer.next();
            }
        } else if (lexer.getLexType().equals(LexType.PLUS) || lexer.getLexType().equals(LexType.MINU)
                || lexer.getLexType().equals(LexType.NOT)) {
            children.add(parseUnaryOp());
            children.add(parseUnaryExp());
        } else {
            children.add(parsePrimaryExp());
        }
        return new UnaryExp(children);
    }

    // UnaryOp -> '+' | '-' | '!'
    private UnaryOp parseUnaryOp() {
        ArrayList<Node> children = new ArrayList<>();
        SyntaxType syntaxType = (lexer.getLexType().equals(LexType.PLUS)) ? SyntaxType.PLUS :
                (lexer.getLexType().equals(LexType.MINU)) ? SyntaxType.MINU : SyntaxType.NOT;
        children.add(new LeafNode(syntaxType, lexer.getToken(), lexer.getLineNumber()));
        lexer.next();
        return new UnaryOp(children);
    }

    // FuncRParams -> Exp {',' Exp}
    private FuncRParams parseFuncRParams() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseExp());
        while (lexer.getLexType().equals(LexType.COMMA)) {
            children.add(new LeafNode(SyntaxType.COMMA, ",", lexer.getLineNumber()));
            lexer.next();
            children.add(parseExp());
        }
        return new FuncRParams(children);
    }

    // MulExp -> UnaryExp | MulExp ('*' | '/' | '%' ) UnaryExp
    private MulExp parseMulExp() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseUnaryExp());
        MulExp mulExp = new MulExp(children);
        while (true) {
            SyntaxType syntaxType = (lexer.getLexType().equals(LexType.MULT)) ? SyntaxType.MULT :
                    (lexer.getLexType().equals(LexType.MOD)) ? SyntaxType.MOD :
                            (lexer.getLexType().equals(LexType.DIV)) ? SyntaxType.DIV : null;
            if (syntaxType == null) {
                break;
            }
            children = new ArrayList<>();
            children.add(mulExp);
            children.add(new LeafNode(syntaxType, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
            children.add(parseUnaryExp());
            mulExp = new MulExp(children);
        }
        return mulExp;
    }

    // AddExp -> MulExp | AddExp ('+' | '-') MulExp
    private AddExp parseAddExp() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseMulExp());
        AddExp addExp = new AddExp(children);
        while (true) {
            SyntaxType syntaxType = (lexer.getLexType().equals(LexType.PLUS)) ? SyntaxType.PLUS :
                    (lexer.getLexType().equals(LexType.MINU)) ? SyntaxType.MINU : null;
            if (syntaxType == null) {
                break;
            }
            children = new ArrayList<>();
            children.add(addExp);
            children.add(new LeafNode(syntaxType, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
            children.add(parseMulExp());
            addExp = new AddExp(children);
        }
        return addExp;
    }

    // RelExp -> AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    private RelExp parseRelExp() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseAddExp());
        RelExp relExp = new RelExp(children);
        while (true) {
            SyntaxType syntaxType = lexer.getLexType().equals(LexType.LSS) ? SyntaxType.LSS :
                    lexer.getLexType().equals(LexType.GRE) ? SyntaxType.GRE :
                            lexer.getLexType().equals(LexType.LEQ) ? SyntaxType.LEQ :
                                    lexer.getLexType().equals(LexType.GEQ) ? SyntaxType.GEQ : null;
            if (syntaxType == null) {
                break;
            }
            children = new ArrayList<>();
            children.add(relExp);
            children.add(new LeafNode(syntaxType, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
            children.add(parseAddExp());
            relExp = new RelExp(children);
        }
        return relExp;
    }

    // EqExp -> EelExp | EqExp ('==' | '!=') RelExp
    private EqExp parseEqExp() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseRelExp());
        EqExp eqExp = new EqExp(children);
        while (true) {
            SyntaxType syntaxType = (lexer.getLexType().equals(LexType.EQL)) ? SyntaxType.EQL :
                    (lexer.getLexType().equals(LexType.NEQ)) ? SyntaxType.NEQ : null;
            if (syntaxType == null) {
                break;
            }
            children = new ArrayList<>();
            children.add(eqExp);
            children.add(new LeafNode(syntaxType, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
            children.add(parseRelExp());
            eqExp = new EqExp(children);
        }
        return eqExp;
    }

    // LAndExp -> EqExp | LAndExp '&&' EqExp
    private LAndExp parseLAndExp() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseEqExp());
        LAndExp lAndExp = new LAndExp(children);
        while (true) {
            SyntaxType syntaxType = (lexer.getLexType().equals(LexType.AND)) ? SyntaxType.AND : null;
            if (syntaxType == null) {
                break;
            }
            children = new ArrayList<>();
            children.add(lAndExp);
            children.add(new LeafNode(syntaxType, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
            children.add(parseEqExp());
            lAndExp = new LAndExp(children);
        }
        return lAndExp;
    }

    // LOrExp -> LAndExp | LOrExp '||' LAndExp
    private LOrExp parseLOrExp() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseLAndExp());
        LOrExp lOrExp = new LOrExp(children);
        while (true) {
            SyntaxType syntaxType = (lexer.getLexType().equals(LexType.OR)) ? SyntaxType.OR : null;
            if (syntaxType == null) {
                break;
            }
            children = new ArrayList<>();
            children.add(lOrExp);
            children.add(new LeafNode(syntaxType, lexer.getToken(), lexer.getLineNumber()));
            lexer.next();
            children.add(parseLAndExp());
            lOrExp = new LOrExp(children);
        }
        return lOrExp;
    }

    // ConstExp -> AddExp
    private ConstExp parseConstExp() {
        ArrayList<Node> children = new ArrayList<>();
        children.add(parseAddExp());
        return new ConstExp(children);
    }

    /**
     * test function
     */
    public String test() {
        return parseCompUnit().toString();
    }
}
