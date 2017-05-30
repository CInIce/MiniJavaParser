package visitor;

import parser.MiniJavaBaseVisitor;
import parser.MiniJavaParser;
import symboltable.Method;
import symboltable.SymbolTable;
import symboltable.Variable;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class TypeCheckVisitor extends MiniJavaBaseVisitor<String> {

    SymbolTable symbolTable;
    symboltable.Class currentClass;
    symboltable.Method currentMethod;

    public TypeCheckVisitor(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    @Override
    public String visitGoal(MiniJavaParser.GoalContext ctx) {
        return super.visitGoal(ctx);
    }

    @Override
    public String visitMainClass(MiniJavaParser.MainClassContext ctx) {
        currentClass = symbolTable.getClass(ctx.identifier(0).getText());
        visitChildren(ctx);
        currentClass = null;
        return null;
    }

    @Override
    public String visitClassDeclaration(MiniJavaParser.ClassDeclarationContext ctx) {
        currentClass = symbolTable.getClass(ctx.identifier(0).getText());
        super.visitClassDeclaration(ctx);
        currentClass = null;
        return null;
    }

    @Override
    public String visitMethodDeclaration(MiniJavaParser.MethodDeclarationContext ctx) {
        currentMethod = currentClass.getMethod(ctx.identifier().getText());
        visitType(ctx.type());
        super.visitMethodDeclaration(ctx);
        currentMethod = null;
        return null;
    }

    @Override
    public String visitVarDeclaration(MiniJavaParser.VarDeclarationContext ctx) {
        return visitType(ctx.type());
    }

    @Override
    public String visitArgumentList(MiniJavaParser.ArgumentListContext ctx) {
        for (MiniJavaParser.TypeContext type : ctx.type()) {
            visitType(type);
        }
        return null;
    }

    @Override
    public String visitExpression(MiniJavaParser.ExpressionContext ctx) {
        if (ctx.classCallExp != null) {
            String classCallType = visitExpression(ctx.classCallExp);
            String methodName = ctx.identifier().getText();
            if (symbolTable.getClass(classCallType).containsMethod(methodName)) {
                Method method = symbolTable.getClass(classCallType).getMethod(methodName);
                List<Variable> args = Collections.list(method.getParams());
                List<MiniJavaParser.ExpressionContext> expressions = ctx.expression();
                if (ctx.expression().size() - 1 == args.size()) {
                    for (int i = 1; i < ctx.expression().size(); i++) {
                        String paramType = visitExpression(ctx.expression(i));
                        if (!paramType.equals(visitType(args.get(i - 1).type()))) {
                            throw new TypeNotPresentException(paramType, null);
                        }
                    }
                    return visitType(method.type());
                }
            }
        }
        if (ctx.expId != null) {
            if (currentMethod != null) {
                if (currentMethod.containsVar(ctx.expId.getText())) {
                    return visitType(currentMethod.getVar(ctx.expId.getText()).type());
                } else if (currentMethod.containsParam(ctx.expId.getText())) {
                    return visitType(currentMethod.getParam(ctx.expId.getText()).type());
                } else if (currentClass.containsVar(ctx.expId.getText())) {
                    return visitType(currentClass.getVar(ctx.expId.getText()).type());
                }
            } else {
                return visitType(currentClass.getVar(ctx.expId.getText()).type());
            }
//            symbolTable.getClass(ctx.expId)
        }

        if (ctx.newId != null) {
            return ctx.identifier().getText();
        }


        if (ctx.thisExp != null) {
            return currentClass.getId();
        }
        //
        if (ctx.op != null) {
            String expType1;
            switch (ctx.op.getText()) {

                case "&&":
                    expType1 = visitExpression(ctx.opExp1);
                    if (expType1.equals(visitExpression(ctx.opExp2)) && expType1.equals("boolean")) {
                        return "boolean";
                    }
                    break;
                case "<":
                    expType1 = visitExpression(ctx.opExp1);
                    if (expType1.equals(visitExpression(ctx.opExp2)) && expType1.equals("int")) {
                        return "boolean";
                    }
                case "-":
                case "*":
                case "+":
                    expType1 = visitExpression(ctx.opExp1);
                    if (expType1.equals(visitExpression(ctx.opExp2)) && expType1.equals("int")) {
                        return "int";
                    }

                    break;
            }
        } else if (ctx.arrayExp != null) {
            if (visitExpression(ctx.indexExp).equals("int")) {
                return getArrayType(visitExpression(ctx.arrayExp));
            }

        } else if (ctx.sizeExp != null) {
            if (visitExpression(ctx.sizeExp).equals("int[]")) {
                return "int";
            }
        } else if (ctx.INT() != null) {
            return "int";
        } else if (ctx.BOOLEAN() != null) {
            return "boolean";
        } else if (ctx.newArray != null) {
            if (visitExpression(ctx.newArray).equals("int")) {
                return "int[]";
            }
        } else if (ctx.notExp != null) {
            if (visitExpression(ctx.notExp).equals("boolean")) {
                return "boolean";
            }
        } else if (ctx.primaryExp != null) {
            return visitExpression(ctx.primaryExp);
        }
        return super.visitExpression(ctx);
    }

    @Override
    public String visitStatement(MiniJavaParser.StatementContext ctx) {
        if (ctx.ifExp != null) {
            if (visitExpression(ctx.ifExp).equals("boolean")) {
                return null;
            }
        }

        if (ctx.whileExp != null) {
            if (visitExpression(ctx.whileExp).equals("boolean")) {
                return null;
            }
        }

        if (ctx.assignArray != null) {
            if (currentClass.getId().equals("BBS") && currentMethod != null && currentMethod.getId().equals("Init")) {
                boolean a = false;
            }
            if (visitExpression(ctx.indexExp).equals("int")) {
                String idType = null;
                if (currentMethod.containsParam(ctx.assignArray.getText())) {
                    idType = visitType(currentMethod.getParam(ctx.assignArray.getText()).type());
                } else if (currentMethod.containsVar(ctx.assignArray.getText())) {
                    idType = visitType(currentMethod.getVar(ctx.assignArray.getText()).type());
                } else if (currentClass.containsVar(ctx.assignArray.getText())) {
                    idType = visitType(currentClass.getVar(ctx.assignArray.getText()).type());
                }

                if (idType.matches("int( )*\\[( )*\\]") && visitExpression(ctx.valueExp).equals("int")) {
                    return null;
                }
            }
            throw new TypeNotPresentException("This is not an int[]", null);
        }

        if (ctx.assign != null) {
            String idType = null;
            if (currentMethod.containsParam(ctx.assign.getText())) {
                idType = visitType(currentMethod.getParam(ctx.assign.getText()).type());
            } else if (currentMethod.containsVar(ctx.assign.getText())) {
                idType = visitType(currentMethod.getVar(ctx.assign.getText()).type());
            } else if (currentClass.containsVar(ctx.assign.getText())) {
                idType = visitType(currentClass.getVar(ctx.assign.getText()).type());
            }

            if (!visitExpression(ctx.assignExp).equals(idType)) {
                throw new TypeNotPresentException(idType, null);
            }
        }

        return null;
    }

    @Override
    public String visitType(MiniJavaParser.TypeContext ctx) {
        if (ctx.getText().matches("int|boolean|int( )*\\[( )*\\]") || symbolTable.containsClass(ctx.getText())) {
            return ctx.getText();
        }
        throw new TypeNotPresentException(ctx.getText(), null);
    }

    public String getArrayType(String type) {
        if (type.equals("int[]")) {
            return "int";
        }
        throw new TypeNotPresentException(type, null);
    }
}
