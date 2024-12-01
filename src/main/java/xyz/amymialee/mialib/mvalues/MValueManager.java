package xyz.amymialee.mialib.mvalues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
import java.util.*;

public class MValueManager {
    public static MValueManager INSTANCE;
    public static final Map<Identifier, MValue<?>> MVALUES = new HashMap<>();
    public static final List<MValueCategory> CATEGORIES = new ArrayList<>();
    public final MinecraftServer server;
    public final Path path;

    private MValueManager(@NotNull MinecraftServer server) {
        INSTANCE = this;
        this.server = server;
        this.path = FabricLoader.getInstance().getConfigDir().resolve("mvalues.json");
        this.loadConfig();
        ScoreboardSyncCallback.EVENT.register((player, tracked) -> {
            for (var key : MVALUES.values()) ServerPlayNetworking.send(player, new MValuePayload(key.id, key.writeNbt(new NbtCompound())));
        });
    }

    public static void create(MinecraftServer server) {
        if (INSTANCE != null) return;
        new MValueManager(server);
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
        if (!CATEGORIES.contains(category)) CATEGORIES.add(category);
        category.addValue(mValue);
    }

    public <T> void onChange(MValue<T> value) {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            MValueManager.INSTANCE.saveConfig();
            for (var player : MValueManager.INSTANCE.server.getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(player, new MValuePayload(value.id, value.type.writeNbt(new NbtCompound(), value)));
            }
        } else {
            ClientPlayNetworking.send(new MValuePayload(value.id, value.type.writeNbt(new NbtCompound(), value)));
        }
    }

    public void saveConfig() {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            var json = new JsonObject();
            for (var entry : MVALUES.entrySet()) json.add(entry.getKey().toString(), entry.getValue().writeJson());
            Files.writeString(this.server.getOverworld().getChunkManager().getPersistentStateManager().directory.resolve("mvalues.json"), gson.toJson(json));
        } catch (Exception e) {
            Mialib.LOGGER.info(e.toString());
        }
    }

    protected void loadConfig() {
        try {
            var data = new Gson().fromJson(Files.readString(this.path), JsonObject.class);
            for (var values : MVALUES.entrySet()) {
                if (!data.has(values.getKey().toString())) continue;
                try {
                    values.getValue().readJson(data.get(values.getKey().toString()));
                } catch (Exception e) {
                    Mialib.LOGGER.info("Error loading mvalue data for {}", values.getKey().toString());
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