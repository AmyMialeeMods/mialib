package xyz.amymialee.mialib.util.runnables;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface QuadConsumer<A, B, C, D> {
    void accept(A a, B b, C c, D d);

    default QuadConsumer<A, B, C, D> andThen(@NotNull QuadConsumer<? super A, ? super B, ? super C, ? super D> after) {
        return (a, b, c, d) -> {
            this.accept(a, b, c, d);
            after.accept(a, b, c, d);
        };
    }
}