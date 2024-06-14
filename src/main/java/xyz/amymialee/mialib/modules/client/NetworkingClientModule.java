package xyz.amymialee.mialib.modules.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xyz.amymialee.mialib.networking.FloatyPayload;
import xyz.amymialee.mialib.networking.MValuePayload;

public interface NetworkingClientModule {
    static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FloatyPayload.ID, new FloatyPayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(MValuePayload.ID, new MValuePayload.ClientReceiver());
    }
}