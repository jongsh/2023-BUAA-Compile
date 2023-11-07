package frontend.syntax.ast;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.type.VarType;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.llvmir.value.instr.IcmpInstr;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.SyntaxType;

import java.util.ArrayList;

public class RelExp extends Node {
    public RelExp(ArrayList<Node> children) {
        super(SyntaxType.RelExp, children);
    }

    // RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
    @Override
    public String checkError() {
        StringBuilder error = new StringBuilder();
        for (Node child : children) {
            error.append(child.checkError());
        }
        return error.toString();
    }

    @Override
    public Value genIR() {
        if (children.size() == 1) {
            return children.get(0).genIR();
        } else {
            Value value1 = children.get(0).genIR();
            Value value2 = children.get(2).genIR();
            String op = ((LeafNode) children.get(1)).getContent();
            IcmpInstr icmpInstr = IRBuilder.getInstance().newIcmpInstr(op, value1, value2);
            IRBuilder.getInstance().addInstr(icmpInstr);
            return icmpInstr;
        }
    }
}
