package frontend.semantics;

import frontend.semantics.llvmir.IRBuilder;
import frontend.semantics.llvmir.value.Module;
import frontend.semantics.llvmir.value.Value;
import frontend.semantics.symbol.SymbolManager;
import frontend.semantics.symbol.SymbolTable;
import frontend.syntax.ast.Node;

public class SemanticAnalyzer {
    private final Node ast;

    public SemanticAnalyzer(Node ast) {
        this.ast = ast;
    }

    public String CheckError() {
        return ast.checkError();
    }

    public Module genIR() {
        ast.genIR();
        return IRBuilder.getInstance().getModule();
    }

    /**
     * 语义分析：综合错误处理与中间代码生成的过程
     */
    public Module analyse() {
        String str = CheckError();
        if (str.equals("")) {
            SymbolManager.instance().reset();
            return genIR();
        }
        System.out.println(str);
        throw new Error("源程序出现错误");
    }

    /**
     * 测试错误处理
     */
    public String testCheckError() {
        return CheckError();
    }

    /**
     * 测试中间代码生成，默认源程序无错误
     */
    public String testGenIR() {
        return genIR().toString();
    }
}
