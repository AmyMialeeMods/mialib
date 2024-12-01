package xyz.amymialee.mialib.util.runnables;

import java.util.function.Function;

public class CachedFunction<T, R> implements Function<T, R> {
    private final R value;

    public CachedFunction(R value) {
        this.value = value;
    }

    @Override
    public R apply(T t) {
        return this.value;
    }
}