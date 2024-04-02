package xyz.amymialee.mialib.util.runnables;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface QuinConsumer<A, B, C, D, E> {
    void accept(A a, B b, C c, D d, E e);

    default QuinConsumer<A, B, C, D, E> andThen(@NotNull QuinConsumer<? super A, ? super B, ? super C, ? super D, ? super E> after) {
        return (a, b, c, d, e) -> {
            this.accept(a, b, c, d, e);
            after.accept(a, b, c, d, e);
        };
    }
}