package xyz.amymialee.mialib;

import net.fabricmc.api.DedicatedServerModInitializer;
import xyz.amymialee.mialib.templates.MRegistry;

public class MialibServer implements DedicatedServerModInitializer {
    public @Override void onInitializeServer() {
        MRegistry.tryBuildAll("%s Server".formatted(Mialib.MOD_NAME));
    }
}