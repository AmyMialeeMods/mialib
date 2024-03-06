package xyz.amymialee.mialib;

import net.fabricmc.api.DedicatedServerModInitializer;

public class MiaLibServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        MRegistry.tryBuildAll("Mialib Server");
    }
}