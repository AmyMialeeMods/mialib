package xyz.amymialee.mialib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xyz.amymialee.mialib.mvalues.MValuePayload;
import xyz.amymialee.mialib.networking.FloatyPayload;

public class MialibClient implements ClientModInitializer {
    public @Override void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(FloatyPayload.ID, new FloatyPayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(MValuePayload.ID, new MValuePayload.ClientReceiver());
    }
}