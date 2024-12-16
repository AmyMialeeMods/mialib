package xyz.amymialee.mialib.mvalues;

public abstract class MValueMinMax<T> extends MValueType<T> {
    public abstract MValueMinMax<T> of(T defaultValue, T min, T max);

    public abstract T getMin();

    public abstract T getMax();
}
