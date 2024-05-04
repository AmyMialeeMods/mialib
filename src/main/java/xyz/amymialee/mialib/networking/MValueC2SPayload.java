package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.mvalues.MValue;

public record MValueC2SPayload(Identifier id, NbtCompound compound) implements CustomPayload {
    public static final Id<MValueC2SPayload> ID = CustomPayload.id(Mialib.id("mvalue_sync").toString());
    public static final PacketCodec<RegistryByteBuf, MValueC2SPayload> CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, MValueC2SPayload::id, PacketCodecs.NBT_COMPOUND, MValueC2SPayload::compound, MValueC2SPayload::new);

    public static void send(MValue<?> value) {
        send(value.id, value.writeNbt(new NbtCompound()));
    }

    public static void send(Identifier id, NbtCompound compound) {
        new MValueC2SPayload(id, compound).send();
    }

    public void send() {
        ClientPlayNetworking.send(this);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}