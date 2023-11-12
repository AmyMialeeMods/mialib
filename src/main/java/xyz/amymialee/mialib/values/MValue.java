package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.util.TriConsumer;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class MValue<T> {
    private final SimpleOption<T> option;
    private final ItemStack displayStack;
    private final TriConsumer<JsonObject, Identifier, T> addToJson;
    private final BiFunction<JsonObject, Identifier, T> readFromJson;

    public MValue(Identifier id, SimpleOption<T> option, ItemStack displayStack, TriConsumer<JsonObject, Identifier, T> addToJson, BiFunction<JsonObject, Identifier, T> readFromJson) {
        this.option = option;
        this.displayStack = displayStack;
        this.addToJson = addToJson;
        this.readFromJson = readFromJson;
        MValues.register(id, this);
    }

    protected void addToJson(JsonObject json, Identifier identifier) {
        this.addToJson.accept(json, identifier, this.option.getValue());
    }

    protected void readFromJson(JsonObject json, Identifier identifier) {
        this.option.setValue(this.readFromJson.apply(json, identifier));
    }

    public T getValue() {
        return this.option.getValue();
    }

    public ItemStack getDisplayStack() {
        return this.displayStack;
    }

    public static MValue<Boolean> ofBoolean(Identifier id, String translationKey, boolean defaultValue) {
        return MValueBuilder.ofBooleanBuilder(translationKey, defaultValue).build(id);
    }

    public static MValue<Boolean> ofBoolean(Identifier id, String translationKey, boolean defaultValue, ItemStack displayStack) {
        return MValueBuilder.ofBooleanBuilder(translationKey, defaultValue, displayStack).build(id);
    }

    public static MValue<Boolean> ofBoolean(Identifier id, String translationKey, boolean defaultValue, ItemStack displayStack, Consumer<Boolean> changedCallback) {
        return MValueBuilder.ofBooleanBuilder(translationKey, defaultValue, displayStack, changedCallback).build(id);
    }

    public static MValue<Integer> ofInteger(Identifier id, String translationKey, int defaultValue, int min, int max) {
        return MValueBuilder.ofIntegerBuilder(translationKey, defaultValue, min, max).build(id);
    }

    public static MValue<Integer> ofInteger(Identifier id, String translationKey, int defaultValue, int min, int max, ItemStack displayStack) {
        return MValueBuilder.ofIntegerBuilder(translationKey, defaultValue, min, max, displayStack).build(id);
    }

    public static MValue<Integer> ofInteger(Identifier id, String translationKey, int defaultValue, int min, int max, ItemStack displayStack, Consumer<Integer> changedCallback) {
        return MValueBuilder.ofIntegerBuilder(translationKey, defaultValue, min, max, displayStack, changedCallback).build(id);
    }

    public static MValue<Double> ofDouble(Identifier id, String translationKey, double defaultValue) {
        return MValueBuilder.ofDoubleBuilder(translationKey, defaultValue).build(id);
    }

    public static MValue<Double> ofDouble(Identifier id, String translationKey, double defaultValue, ItemStack displayStack) {
        return MValueBuilder.ofDoubleBuilder(translationKey, defaultValue, displayStack).build(id);
    }

    public static MValue<Double> ofDouble(Identifier id, String translationKey, double defaultValue, ItemStack displayStack, Consumer<Double> changedCallback) {
        return MValueBuilder.ofDoubleBuilder(translationKey, defaultValue, displayStack, changedCallback).build(id);
    }
}