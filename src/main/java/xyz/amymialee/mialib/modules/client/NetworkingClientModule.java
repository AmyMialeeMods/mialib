package xyz.amymialee.mialib.modules.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueManager;
import xyz.amymialee.mialib.mvalues.MValueScreen;

public interface NetworkingClientModule {
    static void init() {
        ClientPlayNetworking.registerGlobalReceiver(MiaLib.id("floaty"), (minecraftClient, playNetworkHandler, packetByteBuf, packetSender) -> {
            var stack = packetByteBuf.readItemStack();
            minecraftClient.execute(() -> minecraftClient.gameRenderer.showFloatingItem(stack));
        });
        ClientPlayNetworking.registerGlobalReceiver(MValue.MVALUE_SYNC, (client, playNetworkHandler, buf, packetSender) -> {
            var id = buf.readIdentifier();
            var nbt = buf.readNbt();
            client.execute(() -> {
                var mValue = MValueManager.get(id);
                if (mValue != null) {
                    mValue.readNbt(nbt);
                    if (client.currentScreen instanceof MValueScreen screen) {
                        screen.refreshWidgets();
                    }
                }
            });
        });
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