package frontend.syntax;

import frontend.lexer.LexType;

public enum SyntaxType {
    NULL, IDENFR, INTCON, STRCON, MAINTK, CONSTTK, INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK, NOT,
    AND, OR, FORTK, GETINTTK, PRINTFTK, RETURNTK, PLUS, MINU, VOIDTK, MULT, DIV, MOD, LSS, LEQ,
    GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA, LPARENT, RPARENT, LBRACK, RBRACK, LBRACE, RBRACE,

    CompUnit, ConstDecl, VarDecl, ConstDef, ConstInitVal, VarDef, InitVal, FuncDef, MainFuncDef,
    FuncType, FuncFParams, FuncFParam, Block, Stmt, ForStmt, Exp, Cond, LVal, PrimaryExp, Number,
    UnaryExp, UnaryOp, FuncRParams, MulExp, AddExp, RelExp, EqExp, LAndExp, LOrExp, ConstExp;

    @Override
    public String toString() {
        return name();
    }
}
