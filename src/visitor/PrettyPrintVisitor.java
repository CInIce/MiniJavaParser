package visitor;

import parser.MiniJavaBaseVisitor;
import parser.MiniJavaParser;

/**
 * Created by Layon on 30/04/17.
 */
public class PrettyPrintVisitor extends MiniJavaBaseVisitor<Void> {
    public StringBuilder stringBuilder = new StringBuilder();
    int ident = 0;
    @Override
    public Void visitGoal(MiniJavaParser.GoalContext ctx) {

        visitMainClass(ctx.mainClass());
        if(ctx.classDeclaration() == null || ctx.classDeclaration().size() == 0) {
            stringBuilder.append("\n\n");
        }else{
            for (int i = 0; i < ctx.classDeclaration().size() ; i++) {
                visitClassDeclaration(ctx.classDeclaration(i));
                if(i < ctx.classDeclaration().size()-1) {
                    stringBuilder.append("\n\n");
                }
            }
        }
        stringBuilder.append("\n");

        return null;
    }

    @Override
    public Void visitMainClass(MiniJavaParser.MainClassContext ctx) {

        identation();
        stringBuilder.append("class ");
        visitIdentifier(ctx.name);
        stringBuilder.append(" {\n");
        ident++;
        identation();
        stringBuilder.append("public static void main ( String [] ");
        visitIdentifier(ctx.identifier(1));
        stringBuilder.append(") {\n");
        ident++;
        identation();
        if(ctx.statement().block != null) {
            visit(ctx.statement().block);
        }else{
            visit(ctx.statement());
        }
        stringBuilder.append("\n");
        ident--;
        identation();
        stringBuilder.append("}");
        ident--;
        identation();
        stringBuilder.append("}");

        return null;
    }

    @Override
    public Void visitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        identation();
        stringBuilder.append("class ");
        visitIdentifier(ctx.name);
        if(ctx.parent != null){
            stringBuilder.append(" extends ");
            visitIdentifier(ctx.parent);
        }
        stringBuilder.append(" {\n");
        ident++;
        for (int i = 0; i <ctx.varDeclaration().size() ; i++) {
            visitVarDeclaration(ctx.varDeclaration(i));
            stringBuilder.append("\n");
        }
        stringBuilder.append("\n");
        for (int i = 0; i <ctx.methodDeclaration().size() ; i++) {
            visitMethodDeclaration(ctx.methodDeclaration(i));
            if(i< ctx.methodDeclaration().size() -1) {
                stringBuilder.append("\n\n");
            }
        }
        stringBuilder.append("\n");
        ident--;
        identation();
        stringBuilder.append("}");


        return null;
    }

    @Override
    public Void visitVarDeclaration(MiniJavaParser.VarDeclarationContext ctx) {
        identation();
        visitType(ctx.type());
        stringBuilder.append(" ");
        visitIdentifier(ctx.identifier());
        stringBuilder.append(";");
        return null;
    }

    @Override
    public Void visitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        identation();
        stringBuilder.append("public ");
        visitType(ctx.type());
        stringBuilder.append(" ");
        visitIdentifier(ctx.identifier());
        stringBuilder.append("(");
        visitArgumentList(ctx.argumentList());
        stringBuilder.append(") {\n");
        ident++;
        for (int i = 0; i < ctx.varDeclaration().size() ; i++) {
            visitVarDeclaration(ctx.varDeclaration(i));
            stringBuilder.append("\n");
        }
        for (int i = 0; i < ctx.statement().size() ; i++) {
            visitStatement(ctx.statement(i));
            stringBuilder.append("\n");
        }
        identation();
        stringBuilder.append("return ");
        visitExpression(ctx.expression());
        stringBuilder.append(";\n");
        ident--;
        identation();
        stringBuilder.append("}");

        return null;
    }

    @Override
    public Void visitArgumentList(MiniJavaParser.ArgumentListContext ctx) {
        for (int i = 0; i < ctx.type().size(); i++) {
            visitType(ctx.type(i));
            stringBuilder.append(" ");
            visitIdentifier(ctx.identifier(i));
            if (i < ctx.type().size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return null;
    }

    @Override
    public Void visitType(MiniJavaParser.TypeContext ctx) {
        return super.visitType(ctx);
    }

    @Override
    public Void visitIntArrayType(MiniJavaParser.IntArrayTypeContext ctx) {
        stringBuilder.append("int []");
        return null;
    }

    @Override
    public Void visitBooleanType(MiniJavaParser.BooleanTypeContext ctx) {
        stringBuilder.append("boolean");
        return null;
    }

    @Override
    public Void visitIntType(MiniJavaParser.IntTypeContext ctx) {
        stringBuilder.append("int");
        return null;
    }

    @Override
    public Void visitStatement(MiniJavaParser.StatementContext ctx) {
        if(ctx.block != null){
            identation().append("{\n");
            ident++;
            for (MiniJavaParser.StatementContext stmt: ctx.statement()) {
                visitStatement(stmt);
                stringBuilder.append("\n");
            }
            ident--;
            identation().append("}");
        }else if(ctx.ifStmt != null){
            identation().append("if (");
            visitExpression(ctx.ifExp);
            stringBuilder.append(") {\n");
            ident++;
            if(ctx.ifStmt.block != null){
                visit(ctx.ifStmt.block);
            }else{
                visit(ctx.ifStmt);
            }
            ident--;
            stringBuilder.append("\n");
            identation().append("} else {");
            stringBuilder.append("\n");
            ident++;
            if(ctx.ifStmt.block != null){
                visit(ctx.ifStmt.block);
            }else{
                visit(ctx.ifStmt);
            }
            ident--;
            stringBuilder.append("\n");
            identation().append("}");

        }else if(ctx.whileExp != null){
            identation().append("while (");
            visitExpression(ctx.whileExp);
            stringBuilder.append(") {\n");
            ident++;
            if(ctx.whileStmt.block != null){
                visit(ctx.whileStmt.block);
            }else{
                visit(ctx.whileStmt);
            }
            ident--;
            stringBuilder.append("\n");
            identation().append("}");
        }else if(ctx.sysoExp != null){
            identation().append("System.out.println(");
            visitExpression(ctx.sysoExp);
            stringBuilder.append(");");
        }else if(ctx.assign != null){
            identation();
            visitIdentifier(ctx.assign);
            stringBuilder.append(" = ");
            visitExpression(ctx.assignExp);
            stringBuilder.append(";");
        }else if(ctx.assignArray != null){
            identation();
            visitIdentifier(ctx.assignArray);
            stringBuilder.append("[");
            visitExpression(ctx.indexExp);
            stringBuilder.append("] = ");
            visitExpression(ctx.valueExp);
            stringBuilder.append(";");
        }
        return null;
    }

    @Override
    public Void visitExpression(MiniJavaParser.ExpressionContext ctx) {
        if(ctx.opExp1 != null){
          visitExpression(ctx.opExp1);
          stringBuilder.append(" ").append(ctx.op.getText()).append(" ");
          visitExpression(ctx.opExp2);
        } else if(ctx.arrayExp != null){
            visitExpression(ctx.arrayExp);
            stringBuilder.append("[");
            visitExpression(ctx.indexExp);
            stringBuilder.append("]");
        } else if(ctx.sizeExp != null){
            visitExpression(ctx.sizeExp);
            stringBuilder.append(".length");
        } else if(ctx.classCallExp != null) {
            visitExpression(ctx.classCallExp);
            stringBuilder.append(".");
            visitIdentifier(ctx.methodName);
            stringBuilder.append("(");
            for (int i = 1; i < ctx.expression().size(); i++) {
                visitExpression(ctx.expression(i));
                if (i < ctx.expression().size() - 1) {
                    stringBuilder.append(", ");
                }

            }
            stringBuilder.append(")");
        }else if(ctx.INT()!=null){
            stringBuilder.append(ctx.INT().getText());
        }else if(ctx.BOOLEAN() !=null){
            stringBuilder.append(ctx.BOOLEAN().getText());

        }else if(ctx.expId != null){
            visitIdentifier(ctx.expId);

        }else if(ctx.newArray != null){
            stringBuilder.append("new int [");
            visitExpression(ctx.newArray);
            stringBuilder.append("]");
        }else if(ctx.newId != null){
            stringBuilder.append("new ");
            visitIdentifier(ctx.newId);
            stringBuilder.append("()");
        }else if(ctx.notExp != null){
            stringBuilder.append("!");
            visitExpression(ctx.notExp);
        }else if(ctx.primaryExp != null){
            stringBuilder.append("(");
            visitExpression(ctx.primaryExp);
            stringBuilder.append(")");
        }

        return null;
    }

    @Override
    public Void visitIdentifier(MiniJavaParser.IdentifierContext ctx) {
        stringBuilder.append(ctx.IDENTIFIER().getText());
        return null;
    }

    public StringBuilder identation(){
        for (int i = 0; i < this.ident; i++){
            stringBuilder.append("\t");
        }
        return stringBuilder;
    }
}
