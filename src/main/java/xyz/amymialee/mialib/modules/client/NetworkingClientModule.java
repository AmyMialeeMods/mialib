package xyz.amymialee.mialib.modules.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xyz.amymialee.mialib.mvalues.MValueManager;
import xyz.amymialee.mialib.mvalues.MValueScreen;
import xyz.amymialee.mialib.networking.FloatyS2CPayload;
import xyz.amymialee.mialib.networking.MValueS2CPayload;

public interface NetworkingClientModule {
    static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FloatyS2CPayload.ID, (payload, context) -> context.client().execute(() -> context.client().gameRenderer.showFloatingItem(payload.stack())));
        ClientPlayNetworking.registerGlobalReceiver(MValueS2CPayload.ID, (payload, context) -> context.client().execute(() -> {
            var mValue = MValueManager.get(payload.id());
            if (mValue != null) {
                mValue.readNbt(payload.compound());
                if (context.client().currentScreen instanceof MValueScreen screen) {
                    screen.refreshWidgets();
                }
            }
        }));
    }
}