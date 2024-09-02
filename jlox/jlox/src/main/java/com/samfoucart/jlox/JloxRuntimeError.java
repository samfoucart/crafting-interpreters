package com.samfoucart.jlox;

public class JloxRuntimeError extends RuntimeException {
    private final Token token;

    public JloxRuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
