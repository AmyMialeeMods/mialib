package xyz.amymialee.mialib.modules;

import xyz.amymialee.mialib.mvalues.MValueManager;
import xyz.amymialee.mialib.networking.AttackingC2SPayload;
import xyz.amymialee.mialib.networking.MValueC2SPayload;
import xyz.amymialee.mialib.networking.UsingC2SPayload;
import xyz.amymialee.mialib.util.MNetworking;

public interface NetworkingModule {
    static void init() {
        MNetworking.registerPacketReceiver(MValueC2SPayload.ID, MValueC2SPayload.CODEC, (payload, context) -> {
            if (!context.player().hasPermissionLevel(4)) return;
            var id = payload.id();
            var nbt = payload.compound();
            context.player().server.execute(() -> {
                var mValue = MValueManager.get(id);
                if (mValue != null) {
                    mValue.readNbt(nbt);
                    mValue.syncAll();
                    MValueManager.saveConfig();
                }
            });
        });
        MNetworking.registerPacketReceiver(AttackingC2SPayload.ID, AttackingC2SPayload.CODEC, (payload, context) -> context.player().server.execute(() -> context.player().mialib$setHoldingAttack(payload.attacking())));
        MNetworking.registerPacketReceiver(UsingC2SPayload.ID, UsingC2SPayload.CODEC, (payload, context) -> context.player().server.execute(() -> context.player().mialib$setHoldingUse(payload.attacking())));
    }
}