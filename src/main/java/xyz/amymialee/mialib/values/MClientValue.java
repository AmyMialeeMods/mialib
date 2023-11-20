package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.values.old.MValueManager;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class MClientValue<T> extends MValue<T> {
    protected BiFunction<MValue<T>, T, Text> tooltipFactory;
    protected BiFunction<MValue<T>, T, Text> valueTextFactory;
    protected SimpleOption<T> option;

    public T getValue() {
        return this.value;
    }

    public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
        return this.getOption().createWidget(options, x, y, width, value -> this.updateClientToServer());
    }

    public ClickableWidget createWidget(GameOptions options, int x, int y, int width, @NotNull Consumer<T> changeCallback) {
        return this.getOption().createWidget(options, x, y, width, changeCallback.andThen(value -> this.updateClientToServer()));
    }

    public void sendValue(T value) {
        var buf = PacketByteBufs.create();
        buf.writeNbt(this.type.addToNbt.apply(new NbtCompound(), this));
        ClientPlayNetworking.send(MiaLib.id("mvaluesync"), buf);
    }

    public SimpleOption<T> getOption() {
        try {
            if (this.option == null) {
                this.option = new SimpleOption<T>("mvalue.%s.%s".formatted(this.id.getNamespace(), this.id.getPath()),
                        (value) -> Tooltip.of((this.tooltipFactory != null ? this.tooltipFactory : this.type.getDefaultValueTextFactory()).apply(this, value)),
                        (text, value) -> (this.valueTextFactory != null ? this.valueTextFactory : this.type.getDefaultValueTextFactory()).apply(this, value),
                        this.type.getCallbacks(),
                        this.defaultValue,
                        this::sendValue);
            }
        } catch (Throwable error) {
            MiaLib.LOGGER.error("MValue type %s has no renderer.".formatted(this.id), error);
            throw error;
        }
        return this.option;
    }

    protected final NbtCompound addToNbt(NbtCompound nbt) {
        return this.type.addToNbt.apply(nbt, this);
    }

    protected final T readFromNbt(NbtCompound nbt) {
        return this.value = this.type.readFromNbt.apply(nbt, this);
    }

    protected final JsonObject addToJson(JsonObject json) {
        return this.type.addToJson.apply(json, this);
    }

    protected final T readFromJson(JsonObject json) {
        return this.value = this.type.readFromJson.apply(json, this);
    }

    @Environment(EnvType.CLIENT)
    public BiFunction<MValue<T>, T, Text> getTooltipFactory() {
        return this.tooltipFactory;
    }

    @Environment(EnvType.CLIENT)
    public void setTooltipFactory(BiFunction<MValue<T>, T, Text> tooltipFactory) {
        this.tooltipFactory = tooltipFactory;
    }

    @Environment(EnvType.CLIENT)
    public BiFunction<MValue<T>, T, Text> getValueTextFactory() {
        return this.valueTextFactory;
    }

    @Environment(EnvType.CLIENT)
    public void setValueTextFactory(BiFunction<MValue<T>, T, Text> valueTextFactory) {
        this.valueTextFactory = valueTextFactory;
    }
}

class MValueType<T> {
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