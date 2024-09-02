package com.samfoucart.jlox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ScanToInterpretTest {
    @Test
    public void nilShouldReturnNull() {
        String input = "nil";

        Scanner scanner = new Scanner(input);
        
        List<Token> tokens = scanner.scanTokens();

        assertEquals(2, tokens.size());
        assertEquals("nil", tokens.get(0).lexeme);
        assertEquals("nil", tokens.get(0).literal);
        assertEquals(TokenType.NIL, tokens.get(0).type);

        Parser parser = new Parser(tokens);

        Expr expression = parser.parse();
        assertInstanceOf(Expr.Literal.class, expression);

        Interpreter interpreter = new Interpreter();

        Object result = interpreter.evaluate(expression);
        assertEquals(null, result);
    }

    @Test
    public void stringNilShouldReturnString() {
        String input = "\"nil\"";

        Scanner scanner = new Scanner(input);
        
        List<Token> tokens = scanner.scanTokens();

        assertEquals(2, tokens.size());
        assertEquals("\"nil\"", tokens.get(0).lexeme);
        assertEquals("nil", tokens.get(0).literal);
        assertEquals(TokenType.STRING, tokens.get(0).type);

        Parser parser = new Parser(tokens);

        Expr expression = parser.parse();
        assertInstanceOf(Expr.Literal.class, expression);

        Interpreter interpreter = new Interpreter();

        Object result = interpreter.evaluate(expression);
        assertEquals("nil", result);
    }

    @Test
    public void page74ShouldEqualResult() {
        double expected = -5617.41;
        String input = "-123 * (45.67)";

        Scanner scanner = new Scanner(input);
        
        List<Token> tokens = scanner.scanTokens();

        // assertEquals(2, tokens.size());
        // assertEquals("\"nil\"", tokens.get(0).lexeme);
        // assertEquals("nil", tokens.get(0).literal);
        // assertEquals(TokenType.STRING, tokens.get(0).type);

        Parser parser = new Parser(tokens);

        Expr expression = parser.parse();
        // assertInstanceOf(Expr.Literal.class, expression);

        Interpreter interpreter = new Interpreter();

        Object result = interpreter.evaluate(expression);
        assertEquals(expected, result);
    }

    @Test
    public void mathExpression1ShouldEqualResult() {
        double expected = 55;
        String input = "(((1 + 2 * 3 + 4)) * (5 - 9 / 3 + 3))";

        Scanner scanner = new Scanner(input);
        
        List<Token> tokens = scanner.scanTokens();

        // assertEquals(2, tokens.size());
        // assertEquals("\"nil\"", tokens.get(0).lexeme);
        // assertEquals("nil", tokens.get(0).literal);
        // assertEquals(TokenType.STRING, tokens.get(0).type);

        Parser parser = new Parser(tokens);

        Expr expression = parser.parse();
        // assertInstanceOf(Expr.Literal.class, expression);

        Interpreter interpreter = new Interpreter();

        Object result = interpreter.evaluate(expression);
        assertEquals(expected, result);
    }
}
