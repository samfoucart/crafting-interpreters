package com.samfoucart.jlox;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Environment {
    private final @Nullable Environment previous;
    private final Map<String, @Nullable Object> values;

    public Environment() {
        values = new HashMap<>();
        previous = null;
    }

    public Environment(Environment previous) {
        values = new HashMap<>();
        this.previous = previous;
    }

    public @Nullable Object getValue(Token name) {
        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        throw new JloxRuntimeError(name, "Undefined Variable: '" + name.lexeme + "'.");
    }

    public Object define(String key, @Nullable Object value) {
        return values.put(key, value);
    }
}
