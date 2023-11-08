package frontend.lexer;

import java.util.ArrayList;

public class Lexer {
    private final String source;                        // input program
    private String token;                               // value of word
    private LexType lexType;                            // type of word
    private int lineNumber;                             // number of line
    private int curPos;                                 // index of source
    private String tokenCopy = null;
    private LexType lexTypeCopy = null;
    private int lineNumberCopy = 0;
    private int curPosCopy = 0;

    public Lexer(String source) {
        this.source = source;
        this.lineNumber = 1;
        this.curPos = 0;
        this.token = null;
        this.lexType = LexType.NULL;
    }

    public String getToken() {
        return this.token;
    }

    public LexType getLexType() {
        return this.lexType;
    }

    public int getLineNumber() {
        return this.lineNumber;
    }

    public void store() {
        curPosCopy = curPos;
        tokenCopy = token;
        lineNumberCopy = lineNumber;
        lexTypeCopy = lexType;
    }

    public void reStore() {
        curPos = curPosCopy;
        token = tokenCopy;
        lineNumber = lineNumberCopy;
        lexType = lexTypeCopy;
    }

    public boolean next() {
        while (curPos < source.length() && Character.isWhitespace(source.charAt(curPos))) {
            if (source.charAt(curPos) == '\n') {
                this.lineNumber++;
            }
            curPos++;
        }

        if (curPos >= source.length()) {
            lexType = LexType.NULL;
            return false;
        }

        if (source.charAt(curPos) == '+') {
            token = "+";
        } else if (source.charAt(curPos) == '-') {
            token = "-";
        } else if (source.charAt(curPos) == '*') {
            token = "*";
        } else if (source.charAt(curPos) == '%') {
            token = "%";
        } else if (source.charAt(curPos) == ';') {
            token = ";";
        } else if (source.charAt(curPos) == ',') {
            token = ",";
        } else if (source.charAt(curPos) == '(') {
            token = "(";
        } else if (source.charAt(curPos) == ')') {
            token = ")";
        } else if (source.charAt(curPos) == '[') {
            token = "[";
        } else if (source.charAt(curPos) == ']') {
            token = "]";
        } else if (source.charAt(curPos) == '{') {
            token = "{";
        } else if (source.charAt(curPos) == '}') {
            token = "}";
        } else if (source.charAt(curPos) == '&') {
            curPos++;
            token = "&&";
        } else if (source.charAt(curPos) == '|') {
            curPos++;
            token = "||";
        } else if (source.charAt(curPos) == '/') {
            token = "/";
            if (curPos + 1 < source.length() && source.charAt(curPos + 1) == '/') {
                while (curPos < source.length() && source.charAt(curPos) != '\n') {
                    curPos++;
                }
                return next();
            } else if (curPos + 1 < source.length() && source.charAt(curPos + 1) == '*') {
                curPos += 2;
                while (curPos < source.length()) {
                    while (curPos < source.length() && source.charAt(curPos) != '*') {
                        if (source.charAt(curPos) == '\n') {
                            lineNumber++;
                        }
                        curPos++;
                    }
                    while (curPos < source.length() && source.charAt(curPos) == '*') {
                        curPos++;
                    }
                    if (curPos < source.length() && source.charAt(curPos) == '/') {
                        curPos++;
                        return next();
                    }
                }
            }
        } else if (source.charAt(curPos) == '=' || source.charAt(curPos) == '<' || source.charAt(curPos) == '>' || source.charAt(curPos) == '!') {
            token = String.valueOf(source.charAt(curPos));
            if (curPos + 1 < source.length() && source.charAt(curPos + 1) == '=') {
                curPos++;
                token += String.valueOf(source.charAt(curPos));
            }
        } else if (source.charAt(curPos) == '"') {
            int temp = curPos + 1;
            while (source.charAt(temp) != '\"') {
                if (source.charAt(temp) == '\n') {
                    lineNumber++;
                }
                temp++;
            }
            token = source.substring(curPos, temp + 1);
            curPos = temp;

//            int nextCurPos = curPos + 1;
//            int temp = curPos + 1;
//            while (source.charAt(temp) != '\n') {
//                if (source.charAt(temp) == '\"') {
//                    nextCurPos = temp;
//                }
//                temp++;
//            }
//            token = source.substring(curPos, nextCurPos + 1);
//            curPos = nextCurPos;
        } else if (Character.isDigit(source.charAt(curPos))) {
            int nextCurPos = curPos;
            while (Character.isDigit(source.charAt(nextCurPos))) {
                nextCurPos++;
            }
            token = source.substring(curPos, nextCurPos);
            curPos = nextCurPos - 1;
        } else if (source.charAt(curPos) == '_' || Character.isLetter(source.charAt(curPos))) {
            int nextCurPos = curPos + 1;
            while (source.charAt(nextCurPos) == '_' || Character.isLetter(source.charAt(nextCurPos)) || Character.isDigit(source.charAt(nextCurPos))) {
                nextCurPos++;
            }
            token = source.substring(curPos, nextCurPos);
            curPos = nextCurPos - 1;
        }

        lexType = LexType.parse(token);
        curPos++;
        return true;
    }

    public ArrayList<LexType> foresee(int times) {
        int curPosTemp = this.curPos;
        String tokenTemp = this.token;
        int lineNumberTemp = this.lineNumber;
        LexType lexTypeTemp = this.lexType;
        ArrayList<LexType> res = new ArrayList<>();
        for (int i = 0; i < times; ++i) {
            this.next();
            res.add(this.lexType);
        }
        this.curPos = curPosTemp;
        this.token = tokenTemp;
        this.lineNumber = lineNumberTemp;
        this.lexType = lexTypeTemp;
        return res;
    }

    /**
     * test function
     */
    public String test() {
        StringBuilder sb = new StringBuilder();
        while (next()) {
            sb.append(" ").append(getLexType()).append(" ").append(getToken()).append("\n");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        return sb.toString();
    }
}
