package com.samfoucart.jlox;

import java.util.HashMap;
import java.util.Map;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Environment {
    private final @Nullable Environment previous;
    private final Map<String, @Nullable Object> state;

    public Environment() {
        state = new HashMap<>();
        previous = null;
    }

    public Environment(Environment previous) {
        state = new HashMap<>();
        this.previous = previous;
    }

    public @Nullable Object getValue(String key) {
        if (state.containsKey(key)) {
            return state.get(key);
        }

        return null;
    }

    public Object put(String key, @Nullable Object value) {
        return state.put(key, value);
    }
}
