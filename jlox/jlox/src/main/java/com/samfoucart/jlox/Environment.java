package com.samfoucart.jlox;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Environment {
    private final @Nullable Environment enclosing;
    private final Map<String, @Nullable Object> values;

    public Environment() {
        values = new HashMap<>();
        enclosing = null;
    }

    public Environment(Environment previous) {
        values = new HashMap<>();
        this.enclosing = previous;
    }

    public @Nullable Object getValue(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        if (enclosing != null) {
            return enclosing.getValue(name);
        }

        throw new JloxRuntimeError(name, "Undefined Variable: '" + name.lexeme + "'.");
    }

    public Object define(String key, @Nullable Object value) {
        return values.put(key, value);
    }

    public void assign(Token name, Object value) {
        if (values.containsKey(name.lexeme)) {
            values.put(name.lexeme, value);
            return;
        }

        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }

        throw new JloxRuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
