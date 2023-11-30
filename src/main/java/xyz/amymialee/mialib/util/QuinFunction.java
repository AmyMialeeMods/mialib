package xyz.amymialee.mialib.util;

@FunctionalInterface
public interface QuinFunction<A, B, C, D, E, F> {
    F apply(A a, B b, C c, D d, E e);
}