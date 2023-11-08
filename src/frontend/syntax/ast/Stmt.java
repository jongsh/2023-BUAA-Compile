package frontend.syntax.ast;

import midend.llvmir.IRBuilder;
import midend.llvmir.value.BasicBlock;
import midend.llvmir.value.Function;
import midend.llvmir.value.GlobalStr;
import midend.llvmir.value.Value;
import midend.llvmir.value.instr.*;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.semantics.symbol.VarSymbol;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;
import java.util.Arrays;

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

    @Override
    public Value genIR() {
        switch (children.get(0).getType()) {
            case LVal:
                Value leftValue = children.get(0).genIR();
                Value rightValue;
                if (children.size() == 4) {
                    rightValue = children.get(2).genIR();
                } else {
                    Function function = IRBuilder.getInstance().newFunction("int", "getint");
                    rightValue = IRBuilder.getInstance().newCallInstr(function);
                    IRBuilder.getInstance().addInstr((Instr) rightValue);
                }
                StoreInstr storeInstr = IRBuilder.getInstance().newStoreInstr(rightValue, leftValue);
                IRBuilder.getInstance().addInstr(storeInstr);
                break;

            case IFTK:
                BasicBlock trueBlock = IRBuilder.getInstance().newBasicBlock();
                BasicBlock falseBlock = (children.size() > 5) ? IRBuilder.getInstance().newBasicBlock() : null;
                BasicBlock leaveBlock = IRBuilder.getInstance().newBasicBlock();

                IRBuilder.getInstance().addContext(trueBlock).addContext(falseBlock).addContext(leaveBlock);
                children.get(2).genIR();  // cond
                IRBuilder.getInstance().cleanContext(3);

                IRBuilder.getInstance().addBasicBlock(trueBlock);
                children.get(4).genIR();   // if stmt
                BRInstr brInstr = IRBuilder.getInstance().newBRInstr(leaveBlock);
                IRBuilder.getInstance().addInstr(brInstr);

                if (falseBlock != null) {
                    IRBuilder.getInstance().addBasicBlock(falseBlock);
                    children.get(6).genIR();  // else stmt
                    brInstr = IRBuilder.getInstance().newBRInstr(leaveBlock);
                    IRBuilder.getInstance().addInstr(brInstr);
                }
                IRBuilder.getInstance().addBasicBlock(leaveBlock);

                break;

            case FORTK:
                children.get(2).genIR(); // ForStmt or ;

                BasicBlock condBlock = (children.get(4).getType().equals(SyntaxType.Cond)
                        || children.get(3).getType().equals(SyntaxType.Cond)) ?
                        IRBuilder.getInstance().newBasicBlock() : null;
                trueBlock = IRBuilder.getInstance().newBasicBlock();
                leaveBlock = IRBuilder.getInstance().newBasicBlock();
                BasicBlock iterBlock = (children.get(children.size() - 3).getType().equals(SyntaxType.ForStmt))
                        ? IRBuilder.getInstance().newBasicBlock() : null;

                // 添加流程控制块上下文信息
                IRBuilder.getInstance().addContext(trueBlock).addContext(null).addContext(leaveBlock);

                if (condBlock != null) {
                    brInstr = IRBuilder.getInstance().newBRInstr((condBlock));
                    IRBuilder.getInstance().addInstr(brInstr);
                    IRBuilder.getInstance().addBasicBlock(condBlock);
                    children.get(3).genIR();
                    children.get(4).genIR();
                } else {
                    brInstr = IRBuilder.getInstance().newBRInstr((trueBlock));
                    IRBuilder.getInstance().addInstr(brInstr);
                }

                IRBuilder.getInstance().cleanContext(3);  // 清理上下文信息

                IRBuilder.getInstance().addBasicBlock(trueBlock);
                IRBuilder.getInstance().addContext(
                        (iterBlock != null) ? iterBlock : (condBlock != null) ? condBlock : trueBlock
                ).addContext(leaveBlock);
                children.get(children.size() - 1).genIR();  // 循环体
                IRBuilder.getInstance().cleanContext(2);  // 清理上下文信息

                if (iterBlock != null) {
                    brInstr = IRBuilder.getInstance().newBRInstr(iterBlock);
                    IRBuilder.getInstance().addInstr(brInstr);
                    IRBuilder.getInstance().addBasicBlock(iterBlock);
                    children.get(children.size() - 3).genIR();  // 循环迭代
                }

                brInstr = IRBuilder.getInstance().newBRInstr((condBlock != null) ? condBlock : trueBlock);
                IRBuilder.getInstance().addInstr(brInstr);

                IRBuilder.getInstance().addBasicBlock(leaveBlock);
                break;

            case Block:
                SymbolManager.instance().createTable(SymbolTable.TableType.BLOCK, true);
                children.get(0).genIR();
                SymbolManager.instance().tracebackTable();
                break;

            case BREAKTK:
                brInstr = IRBuilder.getInstance().newBRInstr(
                        IRBuilder.getInstance().getLeaveBlock()
                );
                IRBuilder.getInstance().addInstr(brInstr);

            case CONTINUETK:
                brInstr = IRBuilder.getInstance().newBRInstr(
                        (IRBuilder.getInstance().getIterBlock() != null) ?
                                IRBuilder.getInstance().getIterBlock() : IRBuilder.getInstance().getTrueBlock()
                );
                IRBuilder.getInstance().addInstr(brInstr);
                break;

            case PRINTFTK:
                Function putStrFunc;
                Function putIntFunc;

                String str = ((LeafNode) children.get(2)).getContent();
                ArrayList<Value> exps = new ArrayList<>();
                for (int i = 4; i < children.size(); i += 2) {
                    if (children.get(i).getType().equals(SyntaxType.Exp)) {
                        exps.add(children.get(i).genIR());
                    }
                }

                int strPos = 1, expPos = 0;
                for (int i = 1; i < str.length() - 1; ++i) {
                    if (str.charAt(i) == '%' || i == str.length() - 2) {
                        String tempStr = str.substring(strPos, (str.charAt(i) == '%') ? i : i + 1);
                        if (!tempStr.equals("")) {
                            GlobalStr globalStr = IRBuilder.getInstance().newGlobalStr(tempStr);

                            IRBuilder.getInstance().addGlobalStr(globalStr);
                            GepInstr gepInstr = IRBuilder.getInstance().newGepInstr(
                                    globalStr, Arrays.asList(IRBuilder.getInstance().newDigit(0),
                                            IRBuilder.getInstance().newDigit(0))
                            );
                            IRBuilder.getInstance().addInstr(gepInstr);
                            putStrFunc = IRBuilder.getInstance().newFunction("void", "putstr");
                            putStrFunc.addOperand(gepInstr);
                            CallInstr callInstr = IRBuilder.getInstance().newCallInstr(putStrFunc);
                            IRBuilder.getInstance().addInstr(callInstr);
                        }
                        strPos = i + 2;
                    }
                    if (str.charAt(i) == '%') {
                        putIntFunc = IRBuilder.getInstance().newFunction("void", "putint");
                        putIntFunc.addOperand(exps.get(expPos++));
                        CallInstr callInstr = IRBuilder.getInstance().newCallInstr(putIntFunc);
                        IRBuilder.getInstance().addInstr(callInstr);
                        i++;  // 跳过%d
                    }
                }
                break;

            case RETURNTK:
                Value retValue = children.get(1).genIR();  // ; or Exp
                RetInstr returnInstr = IRBuilder.getInstance().newRetInstr(retValue);
                IRBuilder.getInstance().addInstr(returnInstr);
                break;

            case Exp:
                children.get(0).genIR();
        }
        return null;
    }
}
