package xyz.amymialee.mialib.networking;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.internal.base.ComponentUpdatePayload;
import org.ladysnake.cca.internal.base.MorePacketCodecs;
import xyz.amymialee.mialib.MiaLib;

@SuppressWarnings("UnstableApiUsage")
public record MValueS2CPayload(Identifier id, NbtCompound compound) implements CustomPayload {
    public static final Id<MValueS2CPayload> ID = CustomPayload.id(MiaLib.id("mvalue_sync").toString());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}