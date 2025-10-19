package com.sabyabhoi.lox;

public class Interpreter implements Expr.Visitor<Object> {

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        return switch (expr.operator.type()) {
            case MINUS -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left - (double) right;
            }
            case SLASH -> {
                checkNumberOperands(expr.operator, left, right);
                double r = (double) right;
                if(r == 0.0) {
                    throw new RuntimeError(expr.operator, "Divisor cannot be zero");
                }
                yield (double) left / r;
            }
            case STAR -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left * (double) right;
            }
            case PLUS -> {
                if ((left instanceof Double l && right instanceof Double r)) {
                    yield l + r;
                }
                if (left instanceof String l && right instanceof String r) {
                    yield l + r;
                }
                if (left instanceof String l && right instanceof Double r) {
                    yield l + r;
                }
                if (left instanceof Double l && right instanceof String r) {
                    yield l + r;
                }
                throw new RuntimeError(expr.operator, "Operands must be two numbers or two strings");
            }
            case GREATER -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left > (double) right;
            }
            case GREATER_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left >= (double) right;
            }
            case LESS -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left < (double) right;
            }
            case LESS_EQUAL -> {
                checkNumberOperands(expr.operator, left, right);
                yield (double) left <= (double) right;
            }
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            default -> null;
        };
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);

        return switch(expr.operator.type()) {
            case MINUS -> {
                checkNumberOperand(expr.operator, right);
                yield -(double)right;
            }
            case BANG -> !isTruthy(right);
            default -> null;
        };
    }

    private void checkNumberOperand(Token operator, Object right) {
        if(right instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be a number");
    }

    private boolean isEqual(Object left, Object right) {
        if(left == null && right == null) return true;
        if(left == null) return false;
        return left.equals(right);
    }

    private boolean isTruthy(Object object) {
        if(object == null) return false;
        if(object instanceof Boolean) return (boolean) object;
        return true;
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    void interpret(Expr expr) {
        try {
            Object value = evaluate(expr);
            System.out.println(stringify(value));
        } catch(RuntimeError e) {
            Lox.runtimeError(e);
        }
    }

    private String stringify(Object value) {
        if(value == null) return "nil";

        if(value instanceof Double) {
            String text = value.toString();
            if(text.endsWith(".0")) {
                return text.substring(0, text.length() - 2);
            }
            return text;
        }

        return value.toString();
    }
}
