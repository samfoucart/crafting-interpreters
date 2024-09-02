package com.samfoucart.jlox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class InterpreterTest {
    @Test
    public void bookSampleShouldInterpret() {
        // arrange
        Expr expression = new Expr.Binary(
            new Expr.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(123)
            ),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(new Expr.Literal(45.67))
        );

        // act
        Interpreter interpreter = new Interpreter();
        Object result = interpreter.evaluate(expression);

        // assert
        assertEquals(-5617.41, (double) result);
    }
}
