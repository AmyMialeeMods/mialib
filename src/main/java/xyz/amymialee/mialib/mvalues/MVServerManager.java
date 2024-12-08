package xyz.amymialee.mialib.mvalues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardSyncCallback;
import xyz.amymialee.mialib.Mialib;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public record MVServerManager(MinecraftServer server) {
    public static final Map<Identifier, MValue<?>> MVALUES = new HashMap<>();
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("mvalues.json");
    public static MVServerManager INSTANCE;

    public MVServerManager(MinecraftServer server) {
        INSTANCE = this;
        this.server = server;
        this.loadConfig();
        ScoreboardSyncCallback.EVENT.register((player, tracked) -> {
            for (var key : MVALUES.values()) ServerPlayNetworking.send(player, new MValuePayload(key.id, key.writeNbt(new NbtCompound())));
        });
    }

    public static void create(MinecraftServer server) {
        if (INSTANCE != null) return;
        new MVServerManager(server);
    }

    public static MValue<?> get(Identifier id) {
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

    public <T> void onChange(@NotNull MValue<T> value) {
        this.saveConfig();
        for (var player : this.server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new MValuePayload(value.id, value.type.writeNbt(new NbtCompound(), value)));
        }
    }

    public void saveConfig() {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            var json = new JsonObject();
            for (var entry : MVALUES.entrySet()) json.add(entry.getKey().toString(), entry.getValue().writeJson());
            Files.writeString(PATH, gson.toJson(json));
        } catch (Exception e) {
            Mialib.LOGGER.info(e.toString());
        }
    }

    private void loadConfig() {
        try {
            var data = new Gson().fromJson(Files.readString(PATH), JsonObject.class);
            for (var entry : MVALUES.entrySet()) {
                if (!data.has(entry.getKey().toString())) continue;
                try {
                    entry.getValue().readJson(data.get(entry.getKey().toString()));
                } catch (Exception e) {
                    Mialib.LOGGER.info("Error loading mvalue data for {}", entry.getKey().toString());
                    Mialib.LOGGER.info(e.toString());
                }
            }
        } catch (FileNotFoundException e) {
            Mialib.LOGGER.info("MValue data not found.");
        } catch (Exception e) {
            Mialib.LOGGER.info("Error loading mvalue data.");
            Mialib.LOGGER.info(e.toString());
        }

    }
}