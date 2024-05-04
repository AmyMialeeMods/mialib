package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import xyz.amymialee.mialib.MiaLib;

public record UsingC2SPayload(boolean attacking) implements CustomPayload {
    public static final Id<UsingC2SPayload> ID = CustomPayload.id(MiaLib.id("using").toString());
    public static final PacketCodec<RegistryByteBuf, UsingC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, UsingC2SPayload::attacking, UsingC2SPayload::new);

    public static void send(boolean using) {
        new UsingC2SPayload(using).sendUsing();
    }

    public void sendUsing() {
        ClientPlayNetworking.send(this);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}