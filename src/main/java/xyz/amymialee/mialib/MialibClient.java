package xyz.amymialee.mialib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xyz.amymialee.mialib.mvalues.MValuePayload;
import xyz.amymialee.mialib.networking.FloatyPayload;
import xyz.amymialee.mialib.templates.MRegistry;

public class MialibClient implements ClientModInitializer {
    public @Override void onInitializeClient() {
        MRegistry.tryBuildAll("%s Client".formatted(Mialib.MOD_NAME));
        ClientPlayNetworking.registerGlobalReceiver(FloatyPayload.ID, new FloatyPayload.Receiver());
        ClientPlayNetworking.registerGlobalReceiver(MValuePayload.ID, new MValuePayload.ClientReceiver());
    }
}