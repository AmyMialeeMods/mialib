package xyz.amymialee.mialib.util;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public interface MNetworking {
    static <T extends CustomPayload> void registerPacketReceiver(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec, ServerPlayNetworking.PlayPayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(id, codec);
        ServerPlayNetworking.registerGlobalReceiver(id, handler);
    }
}