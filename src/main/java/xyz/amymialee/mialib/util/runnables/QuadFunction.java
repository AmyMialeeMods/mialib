package xyz.amymialee.mialib.util.runnables;

@FunctionalInterface
public interface QuadFunction<A, B, C, D, E> {
    E apply(A a, B b, C c, D d);
}