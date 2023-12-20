package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtCompound;

import java.util.function.BiFunction;

public class MValueType<T> {
    public static final MValueType<Boolean> BOOLEAN = new MValueType<>(Codec.BOOL,
            (nbt, mValue) -> {
                nbt.putString("id", mValue.id.toString());
                nbt.putBoolean("value", mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getBoolean("value"),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsBoolean()
    );
    public static final MValueType<Integer> INTEGER = new MValueType<>(Codec.INT,
            (nbt, mValue) -> {
                nbt.putString("id", mValue.id.toString());
                nbt.putInt("value", mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getInt("value"),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsInt()
    );
    public static final MValueType<Long> LONG = new MValueType<>(Codec.LONG,
            (nbt, mValue) -> {
                nbt.putString("id", mValue.id.toString());
                nbt.putLong("value", mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getLong("value"),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsLong()
    );
    public static final MValueType<Float> FLOAT = new MValueType<>(Codec.FLOAT,
            (nbt, mValue) -> {
                nbt.putString("id", mValue.id.toString());
                nbt.putFloat("value", mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getFloat("value"),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsFloat()
    );
    public static final MValueType<Double> DOUBLE = new MValueType<>(Codec.DOUBLE,
            (nbt, mValue) -> {
                nbt.putString("id", mValue.id.toString());
                nbt.putDouble("value", mValue.getValue());
                return nbt;
            },
            (nbt, mValue) -> nbt.getDouble("value"),
            (json, mValue) -> {
                json.addProperty(mValue.id.toString(), mValue.getValue());
                return json;
            },
            (json, mValue) -> json.get(mValue.id.toString()).getAsDouble()
    );
    public final Codec<T> codec;
    public final BiFunction<NbtCompound, MValue<T>, NbtCompound> addToNbt;
    public final BiFunction<NbtCompound, MValue<T>, T> readFromNbt;
    public final BiFunction<JsonObject, MValue<T>, JsonObject> addToJson;
    public final BiFunction<JsonObject, MValue<T>, T> readFromJson;
//    @Environment(EnvType.CLIENT)
//    protected QuadFunction<MValue<T>, Integer, Integer, Integer, ClickableWidget> defaultWidgetFactory;
//    @Environment(EnvType.CLIENT)
//    protected BiFunction<MValue<T>, T, Text> defaultTooltipFactory = (m, v) -> Text.translatable(m.getTranslationKey()).append(Text.literal(": " + v));
//    @Environment(EnvType.CLIENT)
//    protected BiFunction<MValue<T>, T, Text> defaultValueTextFactory = (m, v) -> Text.translatable(m.getTranslationKey()).append(Text.literal(": " + v));

    public MValueType(Codec<T> codec, BiFunction<NbtCompound, MValue<T>, NbtCompound> addToNbt, BiFunction<NbtCompound, MValue<T>, T> readFromNbt, BiFunction<JsonObject, MValue<T>, JsonObject> addToJson, BiFunction<JsonObject, MValue<T>, T> readFromJson) {
        this.codec = codec;
        this.addToNbt = addToNbt;
        this.readFromNbt = readFromNbt;
        this.addToJson = addToJson;
        this.readFromJson = readFromJson;
    }

//    @Environment(EnvType.CLIENT)
//    public ClickableWidget getDefaultWidget(MValue<T> value, int x, int y, int width) {
//        return this.defaultWidgetFactory.apply(value, x, y, width);
//    }
//
//    @Environment(EnvType.CLIENT)
//    public void setDefaultWidgetFactory(QuadFunction<MValue<T>, Integer, Integer, Integer, ClickableWidget> widgetFactory) {
//        this.defaultWidgetFactory = widgetFactory;
//    }
//
//    @Environment(EnvType.CLIENT)
//    public BiFunction<MValue<T>, T, Text> getDefaultTooltipFactory() {
//        return this.defaultTooltipFactory;
//    }
//
//    @Environment(EnvType.CLIENT)
//    public void setDefaultTooltipFactory(BiFunction<MValue<T>, T, Text> defaultTooltipFactory) {
//        this.defaultTooltipFactory = defaultTooltipFactory;
//    }
//
//    @Environment(EnvType.CLIENT)
//    public BiFunction<MValue<T>, T, Text> getDefaultValueTextFactory() {
//        return this.defaultValueTextFactory;
//    }
//
//    @Environment(EnvType.CLIENT)
//    public void setDefaultValueTextFactory(BiFunction<MValue<T>, T, Text> defaultValueTextFactory) {
//        this.defaultValueTextFactory = defaultValueTextFactory;
//    }
}