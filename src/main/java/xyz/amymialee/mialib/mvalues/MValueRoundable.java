package xyz.amymialee.mialib.mvalues;

public abstract class MValueRoundable<T> extends MValueMinMax<T> {
    private final int decimals;

    public MValueRoundable(int decimals) {
        this.decimals = decimals;
    }

    public abstract MValueRoundable<T> of(T defaultValue, T min, T max, int decimals);

    public int getDecimals() {
        return this.decimals;
    }
}
