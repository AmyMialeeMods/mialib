package xyz.amymialee.mialib;

import net.fabricmc.api.DedicatedServerModInitializer;
import xyz.amymialee.mialib.registration.MRegistry;

public class MiaLibServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        MiaLib.LOGGER.info("Building %d MiaLib Registr%s on Server".formatted(MRegistry.REGISTRIES.size(), MRegistry.REGISTRIES.size() == 1 ? "y" : "ies"));
        MRegistry.REGISTRIES.forEach(MRegistry::build);
    }
}