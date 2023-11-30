package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.QuadFunction;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MValue<T> {
    public final Identifier id;
    public final Function<T, ItemStack> displayStack;
    public final MValueType<T> type;
    protected final T defaultValue;
    protected T value;
    @Environment(EnvType.CLIENT)
    protected QuadFunction<MValue<T>, Integer, Integer, Integer, ClickableWidget> widgetFactory;
    @Environment(EnvType.CLIENT)
    protected BiFunction<MValue<T>, T, Text> tooltipFactory;
    @Environment(EnvType.CLIENT)
    protected BiFunction<MValue<T>, T, Text> valueTextFactory;

    public MValue(Identifier id, Function<T, ItemStack> displayStack, MValueType<T> type, T defaultValue) {
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

    public T getScaledValue(double value) {
        throw new RuntimeException("MValue: Tried to get scaled value on non-integer value");
    }

    public double getScaledValue() {
        throw new RuntimeException("MValue: Tried to get scaled value on non-integer value");
    }

    public void setScaledValue(double value) {
        this.setValue(this.getScaledValue(value));
    }

    public void resetValue() {
        this.setValue(this.defaultValue);
    }

    public String getTranslationKey() {
        return "mvalue.%s.%s".formatted(this.id.getNamespace(), this.id.getPath());
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createWidget(int x, int y, int width) {
        return this.getWidget(x, y, width);
    }

    @Environment(EnvType.CLIENT)
    public void sendValue() {
        var buf = PacketByteBufs.create();
        buf.writeNbt(this.type.addToNbt.apply(new NbtCompound(), this));
        System.out.println("SENDING " + this.value + " TO SERVER");
        ClientPlayNetworking.send(MiaLib.id("mvaluesync"), buf);
    }

    protected final NbtCompound addToNbt(NbtCompound nbt) {
        return this.type.addToNbt.apply(nbt, this);
    }

    public final T readFromNbt(NbtCompound nbt) {
        return this.value = this.type.readFromNbt.apply(nbt, this);
    }

    protected final JsonObject addToJson(JsonObject json) {
        return this.type.addToJson.apply(json, this);
    }

    protected final T readFromJson(JsonObject json) {
        return this.value = this.type.readFromJson.apply(json, this);
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget getWidget(int x, int y, int width) {
        if (this.widgetFactory == null) return this.type.defaultWidgetFactory.apply(this, x, y, width);
        return this.widgetFactory.apply(this, x, y, width);
    }

    @Environment(EnvType.CLIENT)
    public void setWidgetFactory(QuadFunction<MValue<T>, Integer, Integer, Integer, ClickableWidget> widgetFactory) {
        this.widgetFactory = widgetFactory;
    }

    @Environment(EnvType.CLIENT)
    public BiFunction<MValue<T>, T, Text> getTooltipFactory() {
        if (this.tooltipFactory == null) return this.type.defaultTooltipFactory;
        return this.tooltipFactory;
    }

    @Environment(EnvType.CLIENT)
    public void setTooltipFactory(BiFunction<MValue<T>, T, Text> tooltipFactory) {
        this.tooltipFactory = tooltipFactory;
    }

    @Environment(EnvType.CLIENT)
    public BiFunction<MValue<T>, T, Text> getValueTextFactory() {
        if (this.valueTextFactory == null) return this.type.defaultValueTextFactory;
        return this.valueTextFactory;
    }

    @Environment(EnvType.CLIENT)
    public void setValueTextFactory(BiFunction<MValue<T>, T, Text> valueTextFactory) {
        this.valueTextFactory = valueTextFactory;
    }
}