package xyz.amymialee.mialib.mvalues;

import org.jetbrains.annotations.NotNull;

public class MValuePercent extends MValueDouble {
    public MValuePercent(double defaultValue, double min, double max) {
        super(defaultValue, min, max, 0);
    }

    @Override
    public @NotNull String getValueAsString(@NotNull MValue<Double> value) {
        return "%.0f%%".formatted(value.get() * 100);
    }
}
