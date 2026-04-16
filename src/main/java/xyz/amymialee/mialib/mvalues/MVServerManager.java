package xyz.amymialee.mialib.mvalues;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class MVServerManager extends MVManager {
    public static final Map<Identifier, MValue<?>> MVALUES = new HashMap<>();
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("mvalues.json");
    public static MVServerManager INSTANCE;

    public MVServerManager() {
        INSTANCE = this;
        loadConfig();
    }

    public static void create() {
        if (INSTANCE != null) return;
        new MVServerManager();
    }

    @Override
    public MValue<?> get(Identifier id) {
        return MVALUES.get(id);
    }

    public static void register(MValue<?> mValue) {
        if (INSTANCE != null) {
            Mialib.LOGGER.error("MValue registered after component initialization, this leads to value loss!");
            throw new RuntimeException("MValue registered after component initialization, this leads to value loss!");
        }
        MVALUES.put(mValue.id, mValue);
    }

    @Override
    public <T> void onChange(@NotNull MValue<T> value) {
        saveConfig();
    }
}