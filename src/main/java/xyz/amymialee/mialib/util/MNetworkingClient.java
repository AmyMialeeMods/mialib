package xyz.amymialee.mialib.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public interface MNetworkingClient {
    static <T extends CustomPayload> void registerPacketReceiver(CustomPayload.Id<T> id, PacketCodec<? super RegistryByteBuf, T> codec, ClientPlayNetworking.PlayPayloadHandler<T> handler) {
        PayloadTypeRegistry.playS2C().register(id, codec);
        ClientPlayNetworking.registerGlobalReceiver(id, handler);
    }
}