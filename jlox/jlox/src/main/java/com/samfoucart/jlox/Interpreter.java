package com.samfoucart.jlox;

import java.util.List;

import com.samfoucart.jlox.Expr.Assign;
import com.samfoucart.jlox.Expr.Binary;
import com.samfoucart.jlox.Expr.Grouping;
import com.samfoucart.jlox.Expr.Literal;
import com.samfoucart.jlox.Expr.Unary;
import com.samfoucart.jlox.Expr.Variable;
import com.samfoucart.jlox.Stmt.Block;
import com.samfoucart.jlox.Stmt.Expression;
import com.samfoucart.jlox.Stmt.If;
import com.samfoucart.jlox.Stmt.Print;
import com.samfoucart.jlox.Stmt.Var;
import com.samfoucart.jlox.Stmt.While;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {
    private Environment globalEnvironment = new Environment();

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (JloxRuntimeError error) {
            Jlox.runtimeError(error);
        }
    }

    // Public Statement methods

    public Void execute(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringify(value));
        return null;
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        Object value = null;
        if (stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }

        globalEnvironment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements, new Environment(globalEnvironment));
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        Object value = evaluate(stmt.condition);
        if (isTruthy(value)) {
            execute(stmt.truthy);
        } else if (!isTruthy(value)) {
            if (stmt.falsey != null) {
                execute(stmt.falsey);
            }
        }
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        Object value = evaluate(stmt.condition);
        while (isTruthy(value)) {
            execute(stmt.loop);
            value = evaluate(stmt.condition);
        }
        return null;
    }

    private void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.globalEnvironment;
        try {
            this.globalEnvironment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.globalEnvironment = previous;
        }
    }

    // Public Expression statements

    public Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public Object visitAssignExpr(Assign expr) {
        Object value = evaluate(expr.value);
        globalEnvironment.assign(expr.name, value);
        return value;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left - (double) right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double) left * (double) right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double) left / (double) right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }

                if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }

                throw new JloxRuntimeError(expr.operator, "Operands must be of same type.");
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case EQUAL_EQUAL:
                return isEqual(left, right);
            case BANG_EQUAL:
                return !isEqual(left, right);
            default:
                break;
        }

        return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        // I rewrote this function from the book.
        // The book doesnt seem to work for integers.
        // I don't know if I made a mistake,
        // or if the book is trying to fix that later.
        if (expr.value == null) {
            return null;
        }

        if (expr.value instanceof Integer) {
            return Double.parseDouble(Integer.toString((Integer) expr.value));
        }

        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = evaluate(expr.right);

        if (expr.operator.type == TokenType.MINUS) {
            checkNumberOperand(expr.operator, right);
            return - (double) right;
        } else if (expr.operator.type == TokenType.BANG) {
            return !isTruthy(right);
        }

        // The book returns null here instead of throwing.
        // This should never happen unless there is a bug in the scanner or parser
        throw new JloxRuntimeError(expr.operator, "Unreachable statement");
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return globalEnvironment.getValue(expr.name);
    }
    
    private boolean isEqual(Object left, Object right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null) {
            return false;
        }

        return left.equals(right);
    }

    private boolean isTruthy(Object object) {
        if (object == null) {
            return false;
        }

        if (object instanceof Boolean) {
            return (boolean) object;
        }

        return true;
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) {
            return;
        }

        throw new JloxRuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) {
            return;
        }

        throw new JloxRuntimeError(operator, "Operands must be numbers");
    }

    private String stringify(Object value) {
        if (value == null) {
            return "nil";
        }

        if (value instanceof Double) {
            String text = value.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return value.toString();
    }
}
