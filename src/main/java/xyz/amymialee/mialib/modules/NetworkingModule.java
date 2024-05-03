package xyz.amymialee.mialib.modules;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueManager;

public interface NetworkingModule {
    static void init() {
        ServerPlayNetworking.registerGlobalReceiver(MValue.MVALUE_SYNC, ((payload, context) -> {
            if (!player.hasPermissionLevel(4)) return;
            var id = buf.readIdentifier();
            var nbt = buf.readNbt();
            server.execute(() -> {
                var mValue = MValueManager.get(id);
                if (mValue != null) {
                    mValue.readNbt(nbt);
                    mValue.syncAll();
                    MValueManager.saveConfig();
                }
            });
        }));
        ServerPlayNetworking.registerGlobalReceiver(MiaLib.id("attacking"), (payload, context) -> {
            var holding = buf.readBoolean();
            server.execute(() -> player.mialib$setHoldingAttack(holding));
        });
        ServerPlayNetworking.registerGlobalReceiver(MiaLib.id("using"), (payload, context) -> {
            var holding = buf.readBoolean();
            server.execute(() -> player.mialib$setHoldingUse(holding));
        });
    }

    static void syncMValue(@NotNull MValue<?> mValue, ServerPlayerEntity player) {
        var buf = PacketByteBufs.create();
        buf.writeIdentifier(mValue.id);
        buf.writeNbt(mValue.writeNbt(new NbtCompound()));
        ServerPlayNetworking.send(player, MValue.MVALUE_SYNC, buf);
    }
}