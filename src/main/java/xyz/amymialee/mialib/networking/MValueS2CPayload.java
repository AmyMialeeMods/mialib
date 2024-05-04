package xyz.amymialee.mialib.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.mvalues.MValue;

public record MValueS2CPayload(Identifier id, NbtCompound compound) implements CustomPayload {
    public static final Id<MValueS2CPayload> ID = CustomPayload.id(MiaLib.id("mvalue_sync").toString());
    public static final PacketCodec<RegistryByteBuf, MValueS2CPayload> CODEC = PacketCodec.tuple(Identifier.PACKET_CODEC, MValueS2CPayload::id, PacketCodecs.NBT_COMPOUND, MValueS2CPayload::compound, MValueS2CPayload::new);

    public static void send(@NotNull MValue<?> value, ServerPlayerEntity ... players) {
        send(value.id, value.writeNbt(new NbtCompound()), players);
    }

    public static void send(Identifier id, NbtCompound compound, ServerPlayerEntity ... players) {
        new MValueS2CPayload(id, compound).send(players);
    }

    public void send(ServerPlayerEntity @NotNull ... players) {
        for (var player : players) {
            ServerPlayNetworking.send(player, this);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}