package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.function.BiFunction;

public class MValueType<T> {
    public static final MValueType<Boolean> BOOLEAN = new MValueType<>(Codec.BOOL,
            (nbt, mValue) -> {
                nbt.putBoolean(mValue.id.toString(), mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getBoolean(mValue.id.toString()),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsBoolean()
    );
    public static final MValueType<Integer> INTEGER = new MValueType<>(Codec.INT,
            (nbt, mValue) -> {
                nbt.putInt(mValue.id.toString(), mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getInt(mValue.id.toString()),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsInt()
    );
    public static final MValueType<Long> LONG = new MValueType<>(Codec.LONG,
            (nbt, mValue) -> {
                nbt.putLong(mValue.id.toString(), mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getLong(mValue.id.toString()),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsLong()
    );
    public static final MValueType<Float> FLOAT = new MValueType<>(Codec.FLOAT,
            (nbt, mValue) -> {
                nbt.putFloat(mValue.id.toString(), mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getFloat(mValue.id.toString()),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsFloat()
    );
    public static final MValueType<Double> DOUBLE = new MValueType<>(Codec.DOUBLE,
            (nbt, mValue) -> {
                nbt.putDouble(mValue.id.toString(), mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getDouble(mValue.id.toString()),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsDouble()
    );
    public static final MValueType<String> STRING = new MValueType<>(Codec.STRING,
            (nbt, mValue) -> {
                nbt.putString(mValue.id.toString(), mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getString(mValue.id.toString()),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsString()
    );
    public final Codec<T> codec;
    public final BiFunction<NbtCompound, MValue<T>, NbtCompound> addToNbt;
    public final BiFunction<NbtCompound, MValue<T>, T> readFromNbt;
    public final BiFunction<JsonObject, MValue<T>, JsonObject> addToJson;
    public final BiFunction<JsonObject, MValue<T>, T> readFromJson;
    @Environment(EnvType.CLIENT)
    protected BiFunction<MValue<T>, T, Text> defaultTooltipFactory = (m, v) -> Text.translatable(m.getTranslationKey()).append(Text.literal(": " + v));
    @Environment(EnvType.CLIENT)
    protected BiFunction<MValue<T>, T, Text> defaultValueTextFactory = (m, v) -> Text.translatable(m.getTranslationKey()).append(Text.literal(": " + v));
    @Environment(EnvType.CLIENT)
    protected SimpleOption.Callbacks<T> callbacks;

    public MValueType(Codec<T> codec, BiFunction<NbtCompound, MValue<T>, NbtCompound> addToNbt, BiFunction<NbtCompound, MValue<T>, T> readFromNbt, BiFunction<JsonObject, MValue<T>, JsonObject> addToJson, BiFunction<JsonObject, MValue<T>, T> readFromJson) {
        this.codec = codec;
        this.addToNbt = addToNbt;
        this.readFromNbt = readFromNbt;
        this.addToJson = addToJson;
        this.readFromJson = readFromJson;
    }

    @Environment(EnvType.CLIENT)
    public BiFunction<MValue<T>, T, Text> getDefaultTooltipFactory() {
        return this.defaultTooltipFactory;
    }

    @Environment(EnvType.CLIENT)
    public void setDefaultTooltipFactory(BiFunction<MValue<T>, T, Text> defaultTooltipFactory) {
        this.defaultTooltipFactory = defaultTooltipFactory;
    }

    @Environment(EnvType.CLIENT)
    public BiFunction<MValue<T>, T, Text> getDefaultValueTextFactory() {
        return this.defaultValueTextFactory;
    }

    @Environment(EnvType.CLIENT)
    public void setDefaultValueTextFactory(BiFunction<MValue<T>, T, Text> defaultValueTextFactory) {
        this.defaultValueTextFactory = defaultValueTextFactory;
    }

    @Environment(EnvType.CLIENT)
    public SimpleOption.Callbacks<T> getCallbacks() {
        return this.callbacks;
    }

    @Environment(EnvType.CLIENT)
    public void setCallbacks(SimpleOption.Callbacks<T> callbacks) {
        this.callbacks = callbacks;
    }
}