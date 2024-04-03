package xyz.amymialee.mialib.util.runnables;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

public class HoldingFunction<T, R> implements Function<T, R> {
    private final R value;

    public HoldingFunction(R value) {
        this.value = value;
    }

    @Override
    public R apply(T t) {
        return this.value;
    }

    @Override
    public <V> @NotNull Function<V, R> compose(@NotNull Function<? super V, ? extends T> before) {
        return (V v) -> this.value;
    }

    @Override
    public <V> @NotNull Function<T, V> andThen(@NotNull Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(this.value);
    }
}