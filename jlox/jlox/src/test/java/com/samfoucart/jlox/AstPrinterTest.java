package com.samfoucart.jlox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AstPrinterTest {
    @Test
    public void bookSampleShouldPrint() {
        String expected = "(* (- 123) (group 45.67))";

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
        String actual = new AstPrinter().print(expression);

        // assert
        assertEquals(expected, actual);
    }
}
