package visitor;

import parser.MiniJavaBaseVisitor;
import parser.MiniJavaParser;
import symboltable.Method;
import symboltable.SymbolTable;

public class TypeCheckVisitor extends MiniJavaBaseVisitor<Class> {

    SymbolTable symbolTable;
    symboltable.Class currentClass;
    symboltable.Method currentMethod;

    public TypeCheckVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public Class visitGoal(MiniJavaParser.GoalContext ctx) {
        return super.visitGoal(ctx);
    }

    @Override
    public Class visitMainClass(MiniJavaParser.MainClassContext ctx) {
        currentClass = symbolTable.getClass(ctx.identifier(0).getText());
        visitChildren(ctx);
        currentClass = null;
        return null;
    }

    @Override
    public Class visitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        currentClass = symbolTable.getClass(ctx.identifier(0).getText());
        super.visitClassDeclaration(ctx);
        currentClass = null;
        return null;
    }

    @Override
    public Class visitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        currentMethod = currentClass.getMethod(ctx.identifier().getText());
        super.visitMethodDeclaration(ctx);
        currentMethod = null;
        return null;
    }

    @Override
    public Class visitExpression(MiniJavaParser.ExpressionContext ctx) {
//        expression:
//        opExp1 = expression op = ( '&&' | '<' | '+' | '-' | '*' ) opExp2 = expression
//                |arrayExp = expression '[' indexExp = expression ']'
//                |sizeExp = expression '.' 'length'
//                --|classCallExp = expression '.' methodName = identifier '(' ( expression ( ',' expression )* )? ')'
//                |INT
//                |BOOLEAN
//                --| expId = identifier
//                --| thisExp = 'this'
//                |'new' 'int' '[' newArray = expression ']'
//                --|'new' newId = identifier '(' ')'
//                |'!' notExp = expression
//                |'(' primaryExp = expression ')'
//        ;
         if(ctx.expId != null){
             if(currentMethod != null) {
                 currentMethod.getVar(ctx.expId.getText());
             }else{
                 return currentClass.getClass();
             }
//            symbolTable.getClass(ctx.expId)
        }
        if(ctx.classCallExp != null){
//            visitExpression(ctx.classCallExp).
        }
        //
        if (ctx.op != null) {
            Class expType1;
            switch (ctx.op.getText()) {

                case "&&":
                    expType1 = visitExpression(ctx.opExp1);
                    if (expType1.equals(visitExpression(ctx.opExp2)) && expType1.equals(Boolean.class)) {
                        return Boolean.class;
                    }
                    break;
                case "<":
                    expType1 = visitExpression(ctx.opExp1);
                    if (expType1.equals(visitExpression(ctx.opExp2)) && expType1.equals(Integer.class)) {
                        return Boolean.class;
                    }
                case "-":
                case "*":
                case "+":
                    expType1 = visitExpression(ctx.opExp1);
                    if (expType1.equals(visitExpression(ctx.opExp2)) && expType1.equals(Integer.class)) {
                        return Integer.class;
                    }

                    break;
            }
        } else if (ctx.arrayExp != null) {
            if (visitExpression(ctx.indexExp).equals(Integer.class)) {
                return getArrayType(visitExpression(ctx.arrayExp));
            }

        } else if (ctx.sizeExp != null) {
            if (visitExpression(ctx.sizeExp).equals(Integer[].class)) {
                return Integer.class;
            }
        } else if (ctx.INT() != null) {
            return Integer.class;
        } else if (ctx.BOOLEAN() != null) {
            return Boolean.class;
        } else if (ctx.newArray != null) {
            if (visitExpression(ctx.newArray).equals(Integer.class)) {
                return Integer[].class;
            }
        } else if (ctx.notExp != null) {
            if (visitExpression(ctx.notExp).equals(Boolean.class)) {
                return Boolean.class;
            }
        } else if (ctx.primaryExp != null) {
            return visitExpression(ctx.primaryExp);
        }
        return super.visitExpression(ctx);
    }

    public Class getArrayType(Class type) {
        if (type.equals(Integer[].class)) {
            return Integer.class;
        }
        throw new TypeNotPresentException(type.getSimpleName(), null);
    }

    public Class classFroTypeContext(MiniJavaParser.TypeContext typeContext){
        if(typeContext.getClass().equals(MiniJavaParser.BooleanTypeContext.class)) {
            return MiniJavaParser.BooleanTypeContext.class;
        }else if(typeContext.getClass().equals(MiniJavaParser.IntTypeContext.class)){
            return MiniJavaParser.IntTypeContext.class;
        }else if(typeContext.getClass().equals(MiniJavaParser.IntArrayTypeContext.class)){
            return MiniJavaParser.IntArrayTypeContext.class;
        }
        return null;
    }
}
