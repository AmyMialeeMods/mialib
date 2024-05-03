package xyz.amymialee.mialib.networking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.MiaLib;

@SuppressWarnings("UnstableApiUsage")
public record MValueS2CPayload(Identifier id, NbtCompound compound) implements CustomPayload {
    public static final Id<MValueS2CPayload> ID = CustomPayload.id(MiaLib.id("mvalue_sync").toString());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}