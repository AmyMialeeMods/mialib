package xyz.amymialee.mialib.values.old;

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
}