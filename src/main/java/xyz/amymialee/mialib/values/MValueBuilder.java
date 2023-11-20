package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.client.option.GameOptions;
import static net.minecraft.client.option.GameOptions.getGenericValueText;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.TriConsumer;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class MValueBuilder<T> {
    private String translationKey = "";
    private SimpleOption.TooltipFactory<T> tooltip = SimpleOption.emptyTooltip();
    private SimpleOption.ValueTextGetter<T> valueTextGetter = (optionText, value) -> GameOptions.getGenericValueText(optionText, Text.translatable(this.translationKey));
    private SimpleOption.Callbacks<T> callbacks = null;
    private Codec<T> codec;
    private T defaultValue;
    private Consumer<T> changedCallback = value -> {};
    private ItemStack displayStack = ItemStack.EMPTY;
    private TriConsumer<JsonObject, Identifier, T> addToJson = (json, identifier, value) -> {};
    private BiFunction<JsonObject, Identifier, T> readFromJson = (json, identifier) -> this.defaultValue;
    private Function<NbtCompound, T> readFromNbt;
    private Consumer<NbtCompound> writeToNbt;

    protected MValueBuilder() {}

    public MValueBuilder<T> translationKey(String translationKey) {
        this.translationKey = translationKey;
        return this;
    }

    public MValueBuilder<T> tooltip(SimpleOption.TooltipFactory<T> tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public MValueBuilder<T> valueTextGetter(SimpleOption.ValueTextGetter<T> valueTextGetter) {
        this.valueTextGetter = valueTextGetter;
        return this;
    }

    public MValueBuilder<T> callbacks(SimpleOption.Callbacks<T> callbacks) {
        this.callbacks = callbacks;
        return this;
    }

    public MValueBuilder<T> codec(Codec<T> codec) {
        this.codec = codec;
        return this;
    }

    public MValueBuilder<T> defaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public MValueBuilder<T> changedCallback(@NotNull Consumer<T> changedCallback) {
        this.changedCallback = changedCallback.andThen(((value) -> MValueManager.saveConfig()));
        return this;
    }

    public MValueBuilder<T> displayStack(ItemStack displayStack) {
        this.displayStack = displayStack;
        return this;
    }

    public MValueBuilder<T> addToJson(TriConsumer<JsonObject, Identifier, T> addToJson) {
        this.addToJson = addToJson;
        return this;
    }

    public MValueBuilder<T> readFromJson(BiFunction<JsonObject, Identifier, T> readFromJson) {
        this.readFromJson = readFromJson;
        return this;
    }

    public MValueBuilder<T> readFromNbt(Function<NbtCompound, T> readFromNbt) {
        this.readFromNbt = readFromNbt;
        return this;
    }

    public MValueBuilder<T> writeToNbt(Consumer<NbtCompound> writeToNbt) {
        this.writeToNbt = writeToNbt;
        return this;
    }

    public MValue<T> build(Identifier id) {
        if (this.callbacks == null) {
            var exception = new RuntimeException("MValueBuilder: callbacks cannot be null");
            MiaLib.LOGGER.error("MValueBuilder: callbacks cannot be null", exception);
            throw exception;
        }
        var option = new SimpleOption<>(this.translationKey, this.tooltip, this.valueTextGetter, this.callbacks, this.codec, this.defaultValue, this.changedCallback);
        return new MValue<>(id, option, this.displayStack, this.addToJson, this.readFromJson) {
            @Override
            public void writeToNbt(NbtCompound nbt) {
                MValueBuilder.this.writeToNbt.accept(nbt);
            }

            @Override
            public void readFromNbt(NbtCompound nbt) {
                this.setValue(MValueBuilder.this.readFromNbt.apply(nbt));
            }
        };
    }

    public static MValueBuilder<Boolean> ofBooleanBuilder(String translationKey, boolean defaultValue) {
        return ofBooleanBuilder(translationKey, defaultValue, ItemStack.EMPTY);
    }

    public static MValueBuilder<Boolean> ofBooleanBuilder(String translationKey, boolean defaultValue, ItemStack displayStack) {
        return ofBooleanBuilder(translationKey, defaultValue, displayStack, value -> {});
    }

    public static MValueBuilder<Boolean> ofBooleanBuilder(String translationKey, boolean defaultValue, ItemStack displayStack, Consumer<Boolean> changedCallback) {
        var callbacks = SimpleOption.BOOLEAN;
        return new MValueBuilder<Boolean>()
                .translationKey(translationKey)
                .valueTextGetter((optionText, value) -> getGenericValueText(optionText, Text.translatable(translationKey).append(": " + value)))
                .callbacks(callbacks)
                .codec(callbacks.codec())
                .defaultValue(defaultValue)
                .changedCallback(changedCallback)
                .codec(Codec.BOOL)
                .displayStack(displayStack)
                .addToJson((json, identifier, value) -> json.addProperty(identifier.toString(), value))
                .readFromJson((json, identifier) -> {
                    if (json.has(identifier.toString())) {
                        return json.get(identifier.toString()).getAsBoolean();
                    }
                    return defaultValue;
                })
                .writeToNbt(nbt -> nbt.putBoolean("value", defaultValue))
                .readFromNbt(nbt -> nbt.getBoolean("value"));
    }

    public static MValueBuilder<Integer> ofIntegerBuilder(String translationKey, int defaultValue, int min, int max) {
        return ofIntegerBuilder(translationKey, defaultValue, min, max, ItemStack.EMPTY);
    }

    public static MValueBuilder<Integer> ofIntegerBuilder(String translationKey, int defaultValue, int min, int max, ItemStack displayStack) {
        return ofIntegerBuilder(translationKey, defaultValue, min, max, displayStack, value -> {});
    }

    public static MValueBuilder<Integer> ofIntegerBuilder(String translationKey, int defaultValue, int min, int max, ItemStack displayStack, Consumer<Integer> changedCallback) {
        var callbacks = new SimpleOption.ValidatingIntSliderCallbacks(min, max);
        return new MValueBuilder<Integer>()
                .translationKey(translationKey)
                .valueTextGetter((optionText, value) -> getGenericValueText(optionText, Text.translatable(translationKey).append(": " + value)))
                .callbacks(callbacks)
                .codec(callbacks.codec())
                .defaultValue(defaultValue)
                .changedCallback(changedCallback)
                .codec(Codec.INT)
                .displayStack(displayStack)
                .addToJson((json, identifier, value) -> json.addProperty(identifier.toString(), value))
                .readFromJson((json, identifier) -> {
                    if (json.has(identifier.toString())) {
                        return json.get(identifier.toString()).getAsInt();
                    }
                    return defaultValue;
                })
                .writeToNbt(nbt -> nbt.putInt("value", defaultValue))
                .readFromNbt(nbt -> nbt.getInt("value"));
    }

    public static MValueBuilder<Double> ofDoubleBuilder(String translationKey, double defaultValue) {
        return ofDoubleBuilder(translationKey, defaultValue, ItemStack.EMPTY);
    }

    public static MValueBuilder<Double> ofDoubleBuilder(String translationKey, double defaultValue, ItemStack displayStack) {
        return ofDoubleBuilder(translationKey, defaultValue, displayStack, value -> {});
    }

    public static MValueBuilder<Double> ofDoubleBuilder(String translationKey, double defaultValue, ItemStack displayStack, Consumer<Double> changedCallback) {
        var callbacks = SimpleOption.DoubleSliderCallbacks.INSTANCE;
        return new MValueBuilder<Double>()
                .translationKey(translationKey)
                .valueTextGetter((optionText, value) -> getGenericValueText(optionText, Text.translatable(translationKey).append(": " + value)))
                .callbacks(callbacks)
                .codec(callbacks.codec())
                .defaultValue(defaultValue)
                .changedCallback(changedCallback)
                .codec(Codec.DOUBLE)
                .displayStack(displayStack)
                .addToJson((json, identifier, value) -> json.addProperty(identifier.toString(), value))
                .readFromJson((json, identifier) -> {
                    if (json.has(identifier.toString())) {
                        return json.get(identifier.toString()).getAsDouble();
                    }
                    return defaultValue;
                })
                .writeToNbt(nbt -> nbt.putDouble("value", defaultValue))
                .readFromNbt(nbt -> nbt.getDouble("value"));
    }
}