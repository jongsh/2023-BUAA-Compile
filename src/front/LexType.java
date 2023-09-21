package front;

public enum LexType {
    NULL, IDENFR, INTCON, STRCON, MAINTK, CONSTTK,
    INTTK, BREAKTK, CONTINUETK, IFTK, ELSETK, NOT,
    AND, OR, FORTK, GETINTTK, PRINTFTK, RETURNTK,
    PLUS, MINU, VOIDTK, MULT, DIV, MOD, LSS, LEQ,
    GRE, GEQ, EQL, NEQ, ASSIGN, SEMICN, COMMA,
    LPARENT, RPARENT, LBRACK, RBRACK, LBRACE, RBRACE;


    @Override
    public String toString() {
        return name();
    }

    public static LexType parse(String str) {
        switch (str) {
            case "main":
                return MAINTK;
            case "const":
                return CONSTTK;
            case "int":
                return INTTK;
            case "break":
                return BREAKTK;
            case "continue":
                return CONTINUETK;
            case "if":
                return IFTK;
            case "else":
                return ELSETK;
            case "for":
                return FORTK;
            case "getint":
                return GETINTTK;
            case "printf":
                return PRINTFTK;
            case "return":
                return RETURNTK;
            case "void":
                return VOIDTK;
            case "!":
                return NOT;
            case "&&":
                return AND;
            case "||":
                return OR;
            case "+":
                return PLUS;
            case "-":
                return MINU;
            case "*":
                return MULT;
            case "/":
                return DIV;
            case "%":
                return MOD;
            case "<":
                return LSS;
            case "<=":
                return LEQ;
            case ">":
                return GRE;
            case ">=":
                return GEQ;
            case "==":
                return EQL;
            case "!=":
                return NEQ;
            case "=":
                return ASSIGN;
            case ";":
                return SEMICN;
            case ",":
                return COMMA;
            case "(":
                return LPARENT;
            case ")":
                return RPARENT;
            case "[":
                return LBRACK;
            case "]":
                return RBRACK;
            case "{":
                return LBRACE;
            case "}":
                return RBRACE;
            default:
                return (str.charAt(0) == '"') ? STRCON :
                        (Character.isDigit(str.charAt(0))) ? INTCON : IDENFR;
        }

    }
}
