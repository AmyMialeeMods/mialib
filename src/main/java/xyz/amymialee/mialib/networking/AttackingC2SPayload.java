package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import xyz.amymialee.mialib.Mialib;

public record AttackingC2SPayload(boolean attacking) implements CustomPayload {
    public static final Id<AttackingC2SPayload> ID = CustomPayload.id(Mialib.id("attacking").toString());
    public static final PacketCodec<RegistryByteBuf, AttackingC2SPayload> CODEC = PacketCodec.tuple(PacketCodecs.BOOL, AttackingC2SPayload::attacking, AttackingC2SPayload::new);

    public static void send(boolean attacking) {
        new AttackingC2SPayload(attacking).sendAttacking();
    }

    public void sendAttacking() {
        ClientPlayNetworking.send(this);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}