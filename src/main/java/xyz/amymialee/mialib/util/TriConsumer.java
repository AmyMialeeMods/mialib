package xyz.amymialee.mialib.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
    void accept(A a, B b, C c);

    default TriConsumer<A, B, C> andThen(@NotNull TriConsumer<? super A, ? super B, ? super C> after) {
        return (a, b, c) -> {
            this.accept(a, b, c);
            after.accept(a, b, c);
        };
    }
}