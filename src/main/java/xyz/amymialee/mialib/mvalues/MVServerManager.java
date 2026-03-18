package xyz.amymialee.mialib.mvalues;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class MVServerManager extends MVManager {
    public static final Map<Identifier, MValue<?>> MVALUES = new HashMap<>();
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("mvalues.json");
    public static MVServerManager INSTANCE;
    private final MinecraftServer server;

    public MVServerManager(MinecraftServer server) {
        INSTANCE = this;
        Mialib.INSTANCE = this;
        this.server = server;
        this.loadConfig();
    }

    public static void syncCallback(ServerPlayerEntity player) {
        for (var key : MVALUES.values()) ServerPlayNetworking.send(player, new MValuePayload(key.id, key.writeNbt(new NbtCompound())));
    }

    public static void create(MinecraftServer server) {
        if (INSTANCE != null) return;
        new MVServerManager(server);
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
        for (var player : this.server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new MValuePayload(value.id, value.type.writeNbt(new NbtCompound(), value)));
        }
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