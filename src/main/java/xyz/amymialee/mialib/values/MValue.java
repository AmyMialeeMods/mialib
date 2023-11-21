package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
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

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MValue<T> {
    public final Identifier id;
    public final Supplier<ItemStack> displayStack;
    public final MValueType<T> type;
    protected final Consumer<T> changedCallback = value -> {};
    protected final T defaultValue;
    protected T value;
    @Environment(EnvType.CLIENT)
    protected BiFunction<MValue<T>, T, Text> tooltipFactory;
    @Environment(EnvType.CLIENT)
    protected BiFunction<MValue<T>, T, Text> valueTextFactory;
    @Environment(EnvType.CLIENT)
    protected SimpleOption<T> option;

    public MValue(Identifier id, Supplier<ItemStack> displayStack, MValueType<T> type, T defaultValue) {
        this.id = id;
        this.displayStack = displayStack;
        this.type = type;
        this.defaultValue = defaultValue;
        this.value = this.defaultValue;
        MValueManager.register(id, this);
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        if (!MValueManager.isFrozen()) {
            var error = new RuntimeException("MValue: Tried to set value before config load");
            MiaLib.LOGGER.error("MValue: Tried to set value before config load", error);
            throw error;
        }
        this.value = value;
        if (MValueManager.SERVER_INSTANCE != null) MValueManager.SERVER_INSTANCE.updateServerToClient(this);
    }

    public void resetValue() {
        this.setValue(this.defaultValue);
    }

    public String getTranslationKey() {
        return "mvalue.%s.%s".formatted(this.id.getNamespace(), this.id.getPath());
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
        return this.getOption().createWidget(options, x, y, width, this::setValue);
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createWidget(GameOptions options, int x, int y, int width, @NotNull Consumer<T> changeCallback) {
        return this.getOption().createWidget(options, x, y, width, changeCallback.andThen(this::setValue));
    }

    @Environment(EnvType.CLIENT)
    public void sendValue(T value) {
        var buf = PacketByteBufs.create();
        buf.writeNbt(this.type.addToNbt.apply(new NbtCompound(), this));
        ClientPlayNetworking.send(MiaLib.id("mvaluesync"), buf);
    }

    @Environment(EnvType.CLIENT)
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