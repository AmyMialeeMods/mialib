package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.networking.MValueC2SPayload;
import xyz.amymialee.mialib.networking.MValueS2CPayload;
import xyz.amymialee.mialib.util.runnables.HoldingFunction;
import xyz.amymialee.mialib.util.runnables.mvalues.MBooleanFunction;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("unused")
public abstract class MValue<T> {
    public static final Identifier MVALUE_SYNC = MiaLib.id("mvalue_sync");
    public final Identifier id;
    public final Function<MValue<T>, ItemStack> stackFunction;

    public MValue(MValueCategory category, Identifier id, Function<MValue<T>, ItemStack> stackFunction) {
        this.id = id;
        this.stackFunction = stackFunction;
        MValueManager.register(category, this);
    }

    public static MValue.@NotNull MValueBoolean ofBoolean(MValueCategory category, Identifier id, Function<MValue<Boolean>, ItemStack> stackSupplier, boolean defaultValue) {
        return new MValueBoolean(category, id, stackSupplier, defaultValue);
    }

    public static MValue.@NotNull MValueInteger ofInteger(MValueCategory category, Identifier id, Function<MValue<Integer>, ItemStack> stackSupplier, int defaultValue) {
        return new MValueInteger(category, id, stackSupplier, defaultValue);
    }

    public static MValue.@NotNull MValueInteger ofInteger(MValueCategory category, Identifier id, Function<MValue<Integer>, ItemStack> stackSupplier, int defaultValue, int min, int max) {
        return new MValueInteger(category, id, stackSupplier, defaultValue, min, max);
    }

    public static MValue.@NotNull MValueLong ofLong(MValueCategory category, Identifier id, Function<MValue<Long>, ItemStack> stackSupplier, long defaultValue) {
        return new MValueLong(category, id, stackSupplier, defaultValue);
    }

    public static MValue.@NotNull MValueLong ofLong(MValueCategory category, Identifier id, Function<MValue<Long>, ItemStack> stackSupplier, long defaultValue, long min, long max) {
        return new MValueLong(category, id, stackSupplier, defaultValue, min, max);
    }

    public static MValue.@NotNull MValueFloat ofFloat(MValueCategory category, Identifier id, Function<MValue<Float>, ItemStack> stackSupplier, float defaultValue) {
        return new MValueFloat(category, id, stackSupplier, defaultValue);
    }

    public static MValue.@NotNull MValueFloat ofFloat(MValueCategory category, Identifier id, Function<MValue<Float>, ItemStack> stackSupplier, float defaultValue, float min, float max) {
        return new MValueFloat(category, id, stackSupplier, defaultValue, min, max);
    }

    public static MValue.@NotNull MValueDouble ofDouble(MValueCategory category, Identifier id, Function<MValue<Double>, ItemStack> stackSupplier, double defaultValue) {
        return new MValueDouble(category, id, stackSupplier, defaultValue);
    }

    public static MValue.@NotNull MValueDouble ofDouble(MValueCategory category, Identifier id, Function<MValue<Double>, ItemStack> stackSupplier, double defaultValue, double min, double max) {
        return new MValueDouble(category, id, stackSupplier, defaultValue, min, max);
    }

    public static MValue.@NotNull MValueBoolean ofBoolean(MValueCategory category, Identifier id, ItemStack stack, boolean defaultValue) {
        return new MValueBoolean(category, id, new HoldingFunction<>(stack), defaultValue);
    }

    public static MValue.@NotNull MValueBoolean ofBoolean(MValueCategory category, Identifier id, ItemStack stackEnabled, ItemStack stackDisabled, boolean defaultValue) {
        return new MValueBoolean(category, id, new MBooleanFunction(stackEnabled, stackDisabled), defaultValue);
    }

    public static MValue.@NotNull MValueInteger ofInteger(MValueCategory category, Identifier id, ItemStack stack, int defaultValue) {
        return new MValueInteger(category, id, new HoldingFunction<>(stack), defaultValue);
    }

    public static MValue.@NotNull MValueInteger ofInteger(MValueCategory category, Identifier id, ItemStack stack, int defaultValue, int min, int max) {
        return new MValueInteger(category, id, new HoldingFunction<>(stack), defaultValue, min, max);
    }

    public static MValue.@NotNull MValueLong ofLong(MValueCategory category, Identifier id, ItemStack stack, long defaultValue) {
        return new MValueLong(category, id, new HoldingFunction<>(stack), defaultValue);
    }

    public static MValue.@NotNull MValueLong ofLong(MValueCategory category, Identifier id, ItemStack stack, long defaultValue, long min, long max) {
        return new MValueLong(category, id, new HoldingFunction<>(stack), defaultValue, min, max);
    }

    public static MValue.@NotNull MValueFloat ofFloat(MValueCategory category, Identifier id, ItemStack stack, float defaultValue) {
        return new MValueFloat(category, id, new HoldingFunction<>(stack), defaultValue);
    }

    public static MValue.@NotNull MValueFloat ofFloat(MValueCategory category, Identifier id, ItemStack stack, float defaultValue, float min, float max) {
        return new MValueFloat(category, id, new HoldingFunction<>(stack), defaultValue, min, max);
    }

    public static MValue.@NotNull MValueDouble ofDouble(MValueCategory category, Identifier id, ItemStack stack, double defaultValue) {
        return new MValueDouble(category, id, new HoldingFunction<>(stack), defaultValue);
    }

    public static MValue.@NotNull MValueDouble ofDouble(MValueCategory category, Identifier id, ItemStack stack, double defaultValue, double min, double max) {
        return new MValueDouble(category, id, new HoldingFunction<>(stack), defaultValue, min, max);
    }

    public String getTranslationKey() {
        return "mvalue.%s.%s".formatted(this.id.getNamespace(), this.id.getPath());
    }

    public String getDescriptionTranslationKey() {
        return "mvalue.%s.%s.desc".formatted(this.id.getNamespace(), this.id.getPath());
    }

    public void syncAll() {
        for (var player : MValueManager.INSTANCE.server.getPlayerManager().getPlayerList()) {
            MValueS2CPayload.send(this, player);
        }
    }

    public ItemStack getStack() {
        return this.stackFunction.apply(this);
    }

    public void setValue(T value) {
        this.setValueInternal(value);
        this.syncAll();
        MValueManager.saveConfig();
    }

    public void sendValue(T value) {
        this.setValueInternal(value);
        MValueC2SPayload.send(this);
    }

    public abstract T getValue();

    protected abstract void setValueInternal(T value);

    public abstract NbtCompound writeNbt(NbtCompound compound);

    public abstract void readNbt(NbtCompound compound);

    public abstract JsonElement writeJson();

    public abstract void readJson(JsonElement json);

    public static class MValueBoolean extends MValue<Boolean> {
        public final boolean defaultValue;
        private boolean value;

        public MValueBoolean(MValueCategory category, Identifier id, Function<MValue<Boolean>, ItemStack> stackFunction, boolean defaultValue) {
            super(category, id, stackFunction);
            this.defaultValue = defaultValue;
            this.value = this.defaultValue;
        }

        @Override
        public Boolean getValue() {
            return this.value;
        }

        @Override
        protected void setValueInternal(Boolean value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public NbtCompound writeNbt(@NotNull NbtCompound compound) {
            compound.putBoolean("value", this.value);
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound) {
            this.value = compound.getBoolean("value");
        }

        @Override
        public JsonElement writeJson() {
            return new JsonPrimitive(this.getValue());
        }

        @Override
        public void readJson(@NotNull JsonElement json) {
            this.setValueInternal(json.getAsBoolean());
        }
    }

    @SuppressWarnings("unused")
    public abstract static class MValueMinMax<T extends Number> extends MValue<T> {
        public MValueMinMax(MValueCategory category, Identifier id, Function<MValue<T>, ItemStack> stackFunction) {
            super(category, id, stackFunction);
        }

        public abstract T getMin();

        public abstract T getMax();
    }

    public static class MValueInteger extends MValueMinMax<Integer> {
        public final int defaultValue;
        public final int min;
        public final int max;
        private int value;

        public MValueInteger(MValueCategory category, Identifier id, Function<MValue<Integer>, ItemStack> stackFunction, int defaultValue) {
            this(category, id, stackFunction, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
        }

        public MValueInteger(MValueCategory category, Identifier id, Function<MValue<Integer>, ItemStack> stackFunction, int defaultValue, int min, int max) {
            super(category, id, stackFunction);
            this.defaultValue = defaultValue;
            this.value = this.defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public Integer getMin() {
            return this.min;
        }

        @Override
        public Integer getMax() {
            return this.max;
        }

        @Override
        public Integer getValue() {
            return this.value;
        }

        @Override
        protected void setValueInternal(Integer value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public NbtCompound writeNbt(@NotNull NbtCompound compound) {
            compound.putInt("value", this.value);
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound) {
            this.value = compound.getInt("value");
        }

        @Override
        public JsonElement writeJson() {
            return new JsonPrimitive(this.getValue());
        }

        @Override
        public void readJson(@NotNull JsonElement json) {
            this.setValueInternal(json.getAsInt());
        }
    }

    public static class MValueLong extends MValueMinMax<Long> {
        public final long defaultValue;
        public final long min;
        public final long max;
        private long value;

        public MValueLong(MValueCategory category, Identifier id, Function<MValue<Long>, ItemStack> stackFunction, long defaultValue) {
            this(category, id, stackFunction, defaultValue, Long.MIN_VALUE, Long.MAX_VALUE);
        }

        public MValueLong(MValueCategory category, Identifier id, Function<MValue<Long>, ItemStack> stackFunction, long defaultValue, long min, long max) {
            super(category, id, stackFunction);
            this.defaultValue = defaultValue;
            this.value = this.defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public Long getMin() {
            return this.min;
        }

        @Override
        public Long getMax() {
            return this.max;
        }

        @Override
        public Long getValue() {
            return this.value;
        }

        @Override
        protected void setValueInternal(Long value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public NbtCompound writeNbt(@NotNull NbtCompound compound) {
            compound.putLong("value", this.value);
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound) {
            this.value = compound.getLong("value");
        }

        @Override
        public JsonElement writeJson() {
            return new JsonPrimitive(this.getValue());
        }

        @Override
        public void readJson(@NotNull JsonElement json) {
            this.setValueInternal(json.getAsLong());
        }
    }

    public static class MValueFloat extends MValueMinMax<Float> {
        public final float defaultValue;
        public final float min;
        public final float max;
        private float value;

        public MValueFloat(MValueCategory category, Identifier id, Function<MValue<Float>, ItemStack> stackFunction, float defaultValue) {
            this(category, id, stackFunction, defaultValue, Float.MIN_VALUE, Float.MAX_VALUE);
        }

        public MValueFloat(MValueCategory category, Identifier id, Function<MValue<Float>, ItemStack> stackFunction, float defaultValue, float min, float max) {
            super(category, id, stackFunction);
            this.defaultValue = defaultValue;
            this.value = this.defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public Float getMin() {
            return this.min;
        }

        @Override
        public Float getMax() {
            return this.max;
        }

        @Override
        public Float getValue() {
            return this.value;
        }

        @Override
        protected void setValueInternal(Float value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public NbtCompound writeNbt(@NotNull NbtCompound compound) {
            compound.putFloat("value", this.value);
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound) {
            this.value = compound.getFloat("value");
        }

        @Override
        public JsonElement writeJson() {
            return new JsonPrimitive(this.getValue());
        }

        @Override
        public void readJson(@NotNull JsonElement json) {
            this.setValueInternal(json.getAsFloat());
        }
    }

    public static class MValueDouble extends MValueMinMax<Double> {
        public final double defaultValue;
        public final double min;
        public final double max;
        private double value;

        public MValueDouble(MValueCategory category, Identifier id, Function<MValue<Double>, ItemStack> stackFunction, double defaultValue) {
            this(category, id, stackFunction, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
        }

        public MValueDouble(MValueCategory category, Identifier id, Function<MValue<Double>, ItemStack> stackFunction, double defaultValue, double min, double max) {
            super(category, id, stackFunction);
            this.defaultValue = defaultValue;
            this.value = this.defaultValue;
            this.min = min;
            this.max = max;
        }

        @Override
        public Double getMin() {
            return this.min;
        }

        @Override
        public Double getMax() {
            return this.max;
        }

        @Override
        public Double getValue() {
            return this.value;
        }

        @Override
        protected void setValueInternal(Double value) {
            this.value = Objects.requireNonNull(value);
        }

        @Override
        public NbtCompound writeNbt(@NotNull NbtCompound compound) {
            compound.putDouble("value", this.value);
            return compound;
        }

        @Override
        public void readNbt(@NotNull NbtCompound compound) {
            this.value = compound.getDouble("value");
        }

        @Override
        public JsonElement writeJson() {
            return new JsonPrimitive(this.getValue());
        }

        @Override
        public void readJson(@NotNull JsonElement json) {
            this.setValueInternal(json.getAsDouble());
        }
    }
}