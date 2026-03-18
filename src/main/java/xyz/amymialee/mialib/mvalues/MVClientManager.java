package xyz.amymialee.mialib.mvalues;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class MVClientManager extends MVManager {
    public static final Map<Identifier, MValue<?>> MVALUES = new HashMap<>();
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("mvalues-client.json");
    public static MVClientManager INSTANCE;

    public MVClientManager() {
        INSTANCE = this;
        MVManager.INSTANCE = this;
        this.loadConfig();
    }

    public static void create() {
        if (INSTANCE != null) return;
        new MVClientManager();
    }

    @Override
    public MValue<?> get(Identifier id) {
        return MVALUES.get(id);
    }

    public static void register(MValueCategory category, MValue<?> mValue) {
        if (INSTANCE != null) {
            Mialib.LOGGER.error("MValue registered after component initialization, this leads to value loss!");
            throw new RuntimeException("MValue registered after component initialization, this leads to value loss!");
        }
        MVALUES.put(mValue.id, mValue);
        category.addValue(mValue);
    }

    @Override
    public <T> void onChange(@NotNull MValue<T> value) {
        this.saveConfig();
    }

    @Override
    public void saveConfig() {
        saveConfig(MVALUES, PATH);
    }

    @Override
    protected void loadConfig() {
        loadConfig(PATH, MVALUES);
    }
}