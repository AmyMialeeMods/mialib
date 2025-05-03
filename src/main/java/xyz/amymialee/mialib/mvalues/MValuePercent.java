package xyz.amymialee.mialib.mvalues;

import org.jetbrains.annotations.NotNull;

public class MValuePercent extends MValueDouble {
    public MValuePercent(double defaultValue, double min, double max) {
        super(defaultValue, min, max, 0);
    }

    public MValuePercent(double defaultValue, double min, double max, int decimals) {
        super(defaultValue, min, max, decimals);
    }

    @Override
    public @NotNull String getValueAsString(@NotNull MValue<Double> value) {
        return "%.02f%%".formatted(value.get() * 100);
    }

    public @NotNull MValueMinMax<Double> of(Double defaultValue) {
        return new MValuePercent(defaultValue, 0, 1);
    }

    @Override
    public @NotNull MValueMinMax<Double> of(Double defaultValue, Double min, Double max) {
        return new MValuePercent(defaultValue, min, max);
    }

    @Override
    public MValueRoundable<Double> of(Double defaultValue, Double min, Double max, int decimals) {
        return new MValuePercent(defaultValue, min, max, decimals);
    }
}
