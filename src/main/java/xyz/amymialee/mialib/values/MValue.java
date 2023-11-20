package xyz.amymialee.mialib.values;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
    public final Identifier id;
    private final SimpleOption<T> option;
    public final ItemStack displayStack;
    private final TriConsumer<JsonObject, Identifier, T> addToJson;
    private final BiFunction<JsonObject, Identifier, T> readFromJson;

    public MValue(Identifier id, SimpleOption<T> option, ItemStack displayStack, TriConsumer<JsonObject, Identifier, T> addToJson, BiFunction<JsonObject, Identifier, T> readFromJson) {
        this.id = id;
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
        if (MValueManager.INSTANCE.server != null) this.updateServerToClient(MValueManager.INSTANCE.server);
    }

    public void updateServerToClient(@NotNull MinecraftServer server) {
        var buf = PacketByteBufs.create();
        var nbt = new NbtCompound();
        nbt.putString("id", String.valueOf(this.id));
        this.writeToNbt(nbt);
        buf.writeNbt(nbt);
        server.getPlayerManager().getPlayerList().forEach((player) -> ServerPlayNetworking.send(player, MiaLib.id("mvaluesync"), buf));
    }

    public void updateServerToClient(@NotNull ServerPlayerEntity player) {
        var buf = PacketByteBufs.create();
        var nbt = new NbtCompound();
        nbt.putString("id", String.valueOf(this.id));
        this.writeToNbt(nbt);
        buf.writeNbt(nbt);
        ServerPlayNetworking.send(player, MiaLib.id("mvaluesync"), buf);
    }

    @Environment(EnvType.CLIENT)
    public void updateClientToServer() {
        var buf = PacketByteBufs.create();
        var nbt = new NbtCompound();
        nbt.putString("id", String.valueOf(this.id));
        this.writeToNbt(nbt);
        buf.writeNbt(nbt);
        ClientPlayNetworking.send(MiaLib.id("mvaluesync"), buf);
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createWidget(GameOptions options, int x, int y, int width) {
        return this.option.createWidget(options, x, y, width, value -> this.updateClientToServer());
    }

    @Environment(EnvType.CLIENT)
    public ClickableWidget createWidget(GameOptions options, int x, int y, int width, @NotNull Consumer<T> changeCallback) {
        return this.option.createWidget(options, x, y, width, changeCallback.andThen(value -> this.updateClientToServer()));
    }

    public abstract void writeToNbt(NbtCompound nbt);

    public abstract void readFromNbt(NbtCompound nbt);

    public static MValue<Boolean> ofBoolean(Identifier id, boolean defaultValue) {
        return MValueBuilder.ofBooleanBuilder(id, defaultValue).build();
    }

    public static MValue<Boolean> ofBoolean(Identifier id, boolean defaultValue, ItemStack displayStack) {
        return MValueBuilder.ofBooleanBuilder(id, defaultValue, displayStack).build();
    }

    public static MValue<Boolean> ofBoolean(Identifier id, boolean defaultValue, ItemStack displayStack, Consumer<Boolean> changedCallback) {
        return MValueBuilder.ofBooleanBuilder(id, defaultValue, displayStack, changedCallback).build();
    }

    public static MValue<Integer> ofInteger(Identifier id, int defaultValue, int min, int max) {
        return MValueBuilder.ofIntegerBuilder(id, defaultValue, min, max).build();
    }

    public static MValue<Integer> ofInteger(Identifier id, int defaultValue, int min, int max, ItemStack displayStack) {
        return MValueBuilder.ofIntegerBuilder(id, defaultValue, min, max, displayStack).build();
    }

    public static MValue<Integer> ofInteger(Identifier id, int defaultValue, int min, int max, ItemStack displayStack, Consumer<Integer> changedCallback) {
        return MValueBuilder.ofIntegerBuilder(id, defaultValue, min, max, displayStack, changedCallback).build();
    }

    public static MValue<Double> ofDouble(Identifier id, double defaultValue) {
        return MValueBuilder.ofDoubleBuilder(id, defaultValue).build();
    }

    public static MValue<Double> ofDouble(Identifier id, double defaultValue, ItemStack displayStack) {
        return MValueBuilder.ofDoubleBuilder(id, defaultValue, displayStack).build();
    }

    public static MValue<Double> ofDouble(Identifier id, double defaultValue, ItemStack displayStack, Consumer<Double> changedCallback) {
        return MValueBuilder.ofDoubleBuilder(id, defaultValue, displayStack, changedCallback).build();
    }
}