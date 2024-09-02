package com.samfoucart.jlox;

import com.samfoucart.jlox.Expr.Binary;
import com.samfoucart.jlox.Expr.Grouping;
import com.samfoucart.jlox.Expr.Literal;
import com.samfoucart.jlox.Expr.Unary;

public class Interpreter implements Expr.Visitor<Object> {

    public void interpret(Expr expr) {
        try {
            Object value = evaluate(expr);
            System.out.println(stringify(value));
        } catch (JloxRuntimeError error) {
            Jlox.runtimeError(error);
        }
    }

    public Object evaluate(Expr expr) {
        return expr.accept(this);
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
