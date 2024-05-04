package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;

public record FloatyS2CPayload(ItemStack stack) implements CustomPayload {
    public static final Id<FloatyS2CPayload> ID = CustomPayload.id(MiaLib.id("floaty").toString());
    public static final PacketCodec<RegistryByteBuf, FloatyS2CPayload> CODEC = PacketCodec.tuple(ItemStack.PACKET_CODEC, FloatyS2CPayload::stack, FloatyS2CPayload::new);

    public static void send(ItemStack stack, ServerPlayerEntity @NotNull ... players) {
        var payload = new FloatyS2CPayload(stack);
        for (var player : players) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}