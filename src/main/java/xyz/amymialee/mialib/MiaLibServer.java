package xyz.amymialee.mialib;

import net.fabricmc.api.DedicatedServerModInitializer;
import xyz.amymialee.mialib.templates.MRegistry;

public class MiaLibServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        MRegistry.tryBuildAll("%s Server".formatted(MiaLib.MOD_NAME));
    }
}