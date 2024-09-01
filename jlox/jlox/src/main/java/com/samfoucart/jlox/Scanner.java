package com.samfoucart.jlox;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@NullMarked
public class Scanner {
    private final String source;
    private final List<Token> tokens;
    private int line;
    private int current;
    private int start;

    private static final Map<String, TokenType> keywords = new HashMap<>(Map.ofEntries(
        Map.entry("and", TokenType.AND),
        Map.entry("class", TokenType.CLASS),
        Map.entry("else", TokenType.ELSE),
        Map.entry("false", TokenType.FALSE),
        Map.entry("for", TokenType.FOR),
        Map.entry("function", TokenType.FUNCTION),
        Map.entry("if", TokenType.IF),
        Map.entry("nil", TokenType.NIL),
        Map.entry("or", TokenType.OR),
        Map.entry("print", TokenType.PRINT),
        Map.entry("return", TokenType.RETURN),
        Map.entry("super", TokenType.SUPER),
        Map.entry("this", TokenType.THIS),
        Map.entry("true", TokenType.TRUE),
        Map.entry("var", TokenType.VAR),
        Map.entry("while", TokenType.WHILE)
    ));

    public Scanner(String source) {
        this.source = source;
        tokens = new ArrayList<>();
        line = 1;
        current = 0;
        start = 0;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;
            case '!':
                if (match('=')) {
                    addToken(TokenType.BANG_EQUAL);
                } else {
                    addToken(TokenType.BANG);
                }
                break;
            case '=':
                if (match('=')) {
                    addToken(TokenType.EQUAL_EQUAL);
                } else {
                    addToken(TokenType.EQUAL);
                }
                break;
            case '>':
                if (match('=')) {
                    addToken(TokenType.GREATER_EQUAL);
                } else {
                    addToken(TokenType.GREATER);
                }
                break;
            case '<':
                if (match('=')) {
                    addToken(TokenType.LESS_EQUAL);
                } else {
                    addToken(TokenType.LESS);
                }
                break;
            case '"':
                string();
                break;
            case '/':
                if (match('/')){
                    // A comment goes until the end of the line
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace
                break;
            case '\n':
                line++;
                break;
            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Jlox.error(line, "Unexpected character.");
                }
                break;
        }
        return;
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) {
            advance();
        }

        String text = source.substring(start, current);
        @Nullable TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }

        addToken(type, text);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        // look for a fractional part
        if (peek() == '.' && isDigit(peekNext())) {
            // consume the .
            advance();

            while (isDigit(peek())) {
                advance();
            }
        }

        Double result = Double.parseDouble(source.substring(start, current));
        addToken(TokenType.NUMBER, result);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            Jlox.error(line, "Unterminated string.");
            return;
        }

        // The closing "
        advance();

        // Trim the surrounding quotes
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, @Nullable Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        char result = source.charAt(current);
        current++;
        return result;
    }

    private boolean match(char c) {
        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(current) == c) {
            ++current;
            return true;
        }

        return false;
    }

    private char peek() {
        if (isAtEnd()) {
            return '\0';
        }

        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }

        return source.charAt(current + 1);
    }
}
