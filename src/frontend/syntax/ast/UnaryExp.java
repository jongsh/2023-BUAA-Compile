package frontend.syntax.ast;

import midend.llvmir.IRBuilder;
import midend.llvmir.value.Digit;
import midend.llvmir.value.Function;
import midend.llvmir.value.Value;
import midend.llvmir.value.instr.AluInstr;
import midend.llvmir.value.instr.CallInstr;
import midend.llvmir.value.instr.Instr;
import frontend.semantics.symbol.FuncSymbol;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class UnaryExp extends Node {
    public UnaryExp(ArrayList<Node> children) {
        super(SyntaxType.UnaryExp, children);
    }

    public ArrayList<Integer> calculate() {
        ArrayList<Integer> values = new ArrayList<>();
        if (children.get(0).getType().equals(SyntaxType.UnaryOp)) {
            values.add(
                    ((UnaryOp) children.get(0)).calculate().get(0) * ((UnaryExp) children.get(1)).calculate().get(0)
            );
        } else {
            values.addAll(((PrimaryExp) children.get(0)).calculate());
        }
        return values;
    }

    // UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' // c d e j
    //        | UnaryOp UnaryExp
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        if (children.get(0).getType().equals(SyntaxType.IDENFR)) {
            String identName = ((LeafNode) children.get(0)).getContent();
            if (SymbolManager.instance().getFuncSymbol(identName) == null) {                // 判断函数名是否已经定义了
                error.append(children.get(0).getLine()).append(" c\n");
            } else if (children.size() > 2 && children.get(2).getType().equals(SyntaxType.FuncRParams)) {
                SymbolManager.instance().createTable(SymbolTable.TableType.FUNC, false, identName);
                error.append(children.get(2).checkError());
                SymbolManager.instance().tracebackTable();
            } else if ((SymbolManager.instance().getFuncSymbol(identName)).getParams().size() > 0) {
                error.append(children.get(0).getLine()).append(" d\n");
            }
            if (!children.get(children.size() - 1).getType().equals(SyntaxType.RPARENT)) {
                error.append(children.get(children.size() - 1).getLine()).append(" j\n");
            }
        } else {
            for (Node child : children) {
                error.append(child.checkError());
            }
        }
        return error.toString();
    }

    @Override
    public Value genIR() {
        if (children.get(0).getType().equals(SyntaxType.IDENFR)) {  // 函数调用
            String funcName = ((LeafNode) children.get(0)).getContent();
            FuncSymbol funcSymbol = SymbolManager.instance().getFuncSymbol(funcName);

            Function function = IRBuilder.getInstance().newFunction(funcSymbol.getType(), funcName);
            CallInstr callInstr = IRBuilder.getInstance().newCallInstr(function);

            if (children.size() > 2 && children.get(2).getType().equals(SyntaxType.FuncRParams)) {
                SymbolManager.instance().createTable(SymbolTable.TableType.FUNC, false, funcName);
                ArrayList<Value> operands = ((FuncRParams) children.get(2)).genIRs();
                callInstr.addArguments(operands);
                SymbolManager.instance().tracebackTable();
            }

            IRBuilder.getInstance().addInstr(callInstr);
            return callInstr;

        } else if (children.get(0).getType().equals(SyntaxType.PrimaryExp)) {  // PrimaryExp
            return children.get(0).genIR();

        } else {  // UnaryOp UnaryExp
            Value retValue = children.get(1).genIR();
            if (children.get(0).searchNode(SyntaxType.NOT) != null) {
                if (retValue instanceof Digit) {
                    retValue = ((Digit) retValue).getNum() != 0 ? IRBuilder.getInstance().newDigit(0)
                            : IRBuilder.getInstance().newDigit(1);
                } else {
                    retValue = IRBuilder.getInstance().newIcmpInstr(
                            "==", retValue, IRBuilder.getInstance().newDigit(0));
                    IRBuilder.getInstance().addInstr((Instr) retValue);
                }
            } else if (children.get(0).searchNode(SyntaxType.MINU) != null) {
                if (retValue instanceof Digit) {
                    retValue = Digit.calculate(IRBuilder.getInstance().newDigit(0), (Digit) retValue, "-");
                } else {
                    AluInstr instr = IRBuilder.getInstance().newAluInstr("-");
                    instr.addOperands(IRBuilder.getInstance().newDigit(0), retValue);
                    retValue = instr;
                    IRBuilder.getInstance().addInstr(instr);
                }
            }
            return retValue;
        }
    }

}
