package xyz.amymialee.mialib.modules.client;

import xyz.amymialee.mialib.mvalues.MValueManager;
import xyz.amymialee.mialib.mvalues.MValueScreen;
import xyz.amymialee.mialib.networking.FloatyPayload;
import xyz.amymialee.mialib.networking.MValueS2CPayload;
import xyz.amymialee.mialib.util.MNetworkingClient;

public interface NetworkingClientModule {
    static void init() {
        MNetworkingClient.registerPacketReceiver(FloatyPayload.ID, FloatyPayload.CODEC, (payload, context) -> context.client().execute(() -> context.client().gameRenderer.showFloatingItem(payload.stack())));
        MNetworkingClient.registerPacketReceiver(MValueS2CPayload.ID, MValueS2CPayload.CODEC, (payload, context) -> context.client().execute(() -> {
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