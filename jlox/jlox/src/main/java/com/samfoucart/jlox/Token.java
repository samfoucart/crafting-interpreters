package com.samfoucart.jlox;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Token {
    final TokenType type;
    final String lexeme;
    final @Nullable Object literal;
    final int line;

    Token(TokenType type, String lexeme, @Nullable Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexeme + " " + literal + " --- line: " + line;
    }
}
