package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.TriConsumer;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class MValue<T> {
    private final SimpleOption<T> option;
    public final ItemStack displayStack;
    private final TriConsumer<JsonObject, Identifier, T> addToJson;
    private final BiFunction<JsonObject, Identifier, T> readFromJson;

    public MValue(Identifier id, SimpleOption<T> option, ItemStack displayStack, TriConsumer<JsonObject, Identifier, T> addToJson, BiFunction<JsonObject, Identifier, T> readFromJson) {
        this.option = option;
        this.displayStack = displayStack;
        this.addToJson = addToJson;
        this.readFromJson = readFromJson;
        MValueManager.register(id, this);
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

    public void setValue(T value) {
        if (!MValueManager.isFrozen()) {
            var error = new RuntimeException("MValue: Tried to set value before config load");
            MiaLib.LOGGER.error("MValue: Tried to set value before config load", error);
            throw error;
        }
        this.option.setValue(value);
        MValueManager.saveConfig();
    }

    public void updateServerToClient(@NotNull MinecraftServer server, Identifier id) {
        var buf = PacketByteBufs.create();
        var nbt = new NbtCompound();
        nbt.putString("id", String.valueOf(id));
        this.writeToNbt(nbt);
        buf.writeNbt(nbt);
        server.getPlayerManager().getPlayerList().forEach((player) -> ServerPlayNetworking.send(player, MiaLib.id("mvaluesync"), buf));
    }

    public void updateServerToClient(@NotNull ServerPlayerEntity player, Identifier id) {
        var buf = PacketByteBufs.create();
        var nbt = new NbtCompound();
        nbt.putString("id", String.valueOf(id));
        this.writeToNbt(nbt);
        buf.writeNbt(nbt);
        ServerPlayNetworking.send(player, MiaLib.id("mvaluesync"), buf);
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
        return this.option.createWidget(options, x, y, width);
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createWidget(GameOptions options, int x, int y, int width, Consumer<T> changeCallback) {
        return this.option.createWidget(options, x, y, width, changeCallback);
    }

    public abstract void writeToNbt(NbtCompound nbt);

    public abstract void readFromNbt(NbtCompound nbt);

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