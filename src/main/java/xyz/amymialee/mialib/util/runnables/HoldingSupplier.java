package xyz.amymialee.mialib.util.runnables;

import java.util.function.Supplier;

public class HoldingSupplier<T> implements Supplier<T> {
    private final T value;

    public HoldingSupplier(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return this.value;
    }
}