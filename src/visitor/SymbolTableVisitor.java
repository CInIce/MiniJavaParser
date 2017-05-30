package visitor;

import parser.MiniJavaBaseVisitor;
import parser.MiniJavaParser;
import symboltable.Class;
import symboltable.SymbolTable;

/**
 * Created by Layon on 30/04/17.
 */
public class SymbolTableVisitor extends MiniJavaBaseVisitor<Void> {
    public SymbolTable symbolTable = new SymbolTable();
    String clazz;
    String method;

    @Override
    public Void visitGoal(MiniJavaParser.GoalContext ctx) {
        return super.visitGoal(ctx);
    }

    @Override
    public Void visitMainClass(MiniJavaParser.MainClassContext ctx) {

        symbolTable.addClass(clazz = ctx.name.IDENTIFIER().getText(), null);
        return super.visitMainClass(ctx);
    }

    @Override
    public Void visitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        symbolTable.addClass(clazz = ctx.name.IDENTIFIER().getText(), ctx.parent == null ? null : ctx.parent.IDENTIFIER().getText());
        return super.visitClassDeclaration(ctx);
    }

    @Override
    public Void visitVarDeclaration(MiniJavaParser.VarDeclarationContext ctx) {
        Class clazz = symbolTable.getClass(this.clazz);
        if (method == null) {
            clazz.addVar(ctx.identifier().getText(), ctx.type());
        } else {
            clazz.getMethod(method).addVar(ctx.identifier().getText(), ctx.type());
        }
        return null;
    }

    @Override
    public Void visitArgumentList(MiniJavaParser.ArgumentListContext ctx) {
        for (int i = 0; i < ctx.type().size(); i++) {
            symbolTable.getClass(clazz).getMethod(method).addParam(ctx.identifier(i).getText(), ctx.type(i));
        }
        return null;
    }

    @Override
    public Void visitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        symbolTable.getClass(clazz).addMethod(method = ctx.identifier().getText(), ctx.type());
        super.visitMethodDeclaration(ctx);
        method = null;
        return null;
    }
}
