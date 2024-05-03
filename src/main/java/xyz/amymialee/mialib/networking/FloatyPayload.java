package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;

public record FloatyPayload(ItemStack stack) implements CustomPayload {
    public static final Id<FloatyPayload> ID = CustomPayload.id(MiaLib.id("floaty").toString());
    public static final PacketCodec<RegistryByteBuf, FloatyPayload> CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, FloatyPayload::stack, FloatyPayload::new);

    public static void send(ItemStack stack, ServerPlayerEntity @NotNull ... players) {
        var payload = new FloatyPayload(stack);
        for (var player : players) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}