import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import parser.MiniJavaLexer;
import parser.MiniJavaParser;
import symboltable.SymbolTable;
import visitor.PrettyPrintVisitor;
import visitor.SymbolTableVisitor;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * Created by Layon on 30/04/17.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Files.newDirectoryStream(Paths.get("supportFiles")).forEach(
                path -> {
                    try {
                        ANTLRInputStream stream = new ANTLRInputStream(Files.newInputStream(path));
                        MiniJavaLexer lexer = new MiniJavaLexer(stream);
                        TokenStream tokenStream = new CommonTokenStream(lexer);
                        MiniJavaParser parser = new MiniJavaParser(tokenStream);
                        MiniJavaParser.GoalContext context = parser.goal();
//                        SymbolTableVisitor symbolTableVisitor = new SymbolTableVisitor();
//                        symbolTableVisitor.visitGoal(context);
//                        SymbolTable table = symbolTableVisitor.symbolTable;
                        PrettyPrintVisitor print = new PrettyPrintVisitor();
                        print.visitGoal(context);
                        System.out.println(print.stringBuilder.toString());
                        System.out.println("\n\n\n///////////////////////////////////////");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

    }
}
