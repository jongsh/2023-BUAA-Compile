package frontend.semantics;

import midend.llvmir.IRBuilder;
import midend.llvmir.value.Module;
import frontend.semantics.symbol.SymbolManager;
import frontend.syntax.ast.Node;

public class SemanticAnalyzer {
    private final Node ast;

    public SemanticAnalyzer(Node ast) {
        this.ast = ast;
    }

    public String checkError() {
        return ast.checkError();
    }

    public Module genIR() {
        SymbolManager.instance().reset();
        ast.genIR();
        return IRBuilder.getInstance().getModule();
    }

    /**
     * 语义分析：综合错误处理与中间代码生成的过程
     */
    public Module analyse() {
        String str = checkError();
        if (str.equals("")) {
            SymbolManager.instance().reset();
            return genIR();
        }
        System.out.println(str);
        throw new Error("源程序出现错误");
    }
}
