import backend.AsmGenerator;
import backend.mips.MipsBuilder;
import backend.mips.MipsProcedure;
import frontend.lexer.Lexer;
import frontend.semantics.SemanticAnalyzer;
import frontend.syntax.Parser;
import midend.llvmir.value.Module;
import midend.optimize.Optimizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Compiler {
    public static void main(String[] args) {
        run("gen mips with error handle");
    }

    private static String inputFromFile(String inputPath) {
        File file = new File(inputPath);
        StringBuilder sb = new StringBuilder();
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine()).append("\n");
            }
            if (sb.length() > 0) {
                sb.delete(sb.length() - 1, sb.length());
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            return "";
        }
    }

    private static void outputToFile(String outPutPath, String content) {
        try {
            FileWriter fileWriter = new FileWriter(outPutPath);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("output get wrong");
        }
    }

    private static void run(String mode) {
        String inputFileName = "testfile.txt";
        switch (mode) {
            case "gen mips with error handle":
                Lexer lexer = new Lexer(inputFromFile(inputFileName));
                // 词法分析输出
                //outputToFile(outputFileName, lexer.test());

                Parser parser = new Parser(lexer);
                // 语法分析输出
                // outputToFile(outputFileName, parser.test());

                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(parser.parseCompUnit());

                // 错误处理输出
                String error = semanticAnalyzer.checkError();
                if (!error.equals("")) {
                    outputToFile("error.txt", error);
                    return;
                }

                // 中间代码生成
                Module module = semanticAnalyzer.genIR();
                outputToFile("llvm_ir.txt", module.toString());

                // 中间代码优化
                Optimizer.optimize(module);
                outputToFile("llvm_ir_opt.txt", module.toString());

                // 目标代码生成
                MipsProcedure procedure = AsmGenerator.getInstance().genMips(module);
                outputToFile("mips.txt", procedure.toString());
        }
    }
}
