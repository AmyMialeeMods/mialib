package xyz.amymialee.mialib.modules.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueManager;
import xyz.amymialee.mialib.mvalues.MValueScreen;
import xyz.amymialee.mialib.networking.FloatyPayload;
import xyz.amymialee.mialib.networking.MValueS2CPayload;

public interface NetworkingClientModule {
    static void init() {
        PayloadTypeRegistry.playS2C().register(FloatyPayload.ID, FloatyPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(FloatyPayload.ID, (payload, context) -> context.client().execute(() -> context.client().gameRenderer.showFloatingItem(payload.stack())));

        PayloadTypeRegistry.playS2C().register(MValueS2CPayload.ID, MValueS2CPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(MValueS2CPayload.ID, (payload, context) -> context.client().execute(() -> {
            var mValue = MValueManager.get(payload.id());
            if (mValue != null) {
                mValue.readNbt(payload.buf());
                if (context.client().currentScreen instanceof MValueScreen screen) {
                    screen.refreshWidgets();
                }
            }
        }));
    }

    static void sendAttacking(boolean attacking) {
        var buf = PacketByteBufs.create();
        buf.writeBoolean(attacking);
        ClientPlayNetworking.send(MiaLib.id("attacking"), buf);
    }

    static void sendUsing(boolean using) {
        var buf = PacketByteBufs.create();
        buf.writeBoolean(using);
        ClientPlayNetworking.send(MiaLib.id("using"), buf);
    }

    static void sendMValueChange(@NotNull MValue<?> mValue) {
        var buf = PacketByteBufs.create();
        buf.writeIdentifier(mValue.id);
        buf.writeNbt(mValue.writeNbt(new NbtCompound()));
        ClientPlayNetworking.send(MValue.MVALUE_SYNC, buf);
    }
}