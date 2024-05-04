package xyz.amymialee.mialib.mvalues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardSyncCallback;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.networking.MValueS2CPayload;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MValueManager {
    private static final Path configFile = FabricLoader.getInstance().getConfigDir().resolve("mvalues.json");
    private static final Map<Identifier, MValue<?>> MVALUES = new HashMap<>();
    public static final List<MValueCategory> CATEGORIES = new ArrayList<>();
    public static MValueManager INSTANCE;
    public final MinecraftServer server;

    private MValueManager(MinecraftServer server) {
        this.server = server;
        INSTANCE = this;
        ScoreboardSyncCallback.EVENT.register((player, tracked) -> {
            for (var key : MVALUES.values()) {
                MValueS2CPayload.send(key, player);
            }
        });
        loadConfig();
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
        category.values.add(mValue);
    }

    public static void saveConfig() {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            var json = new JsonObject();
            for (var values : MVALUES.entrySet()) {
                json.add(values.getKey().toString(), values.getValue().writeJson());
            }
            var jsonData = gson.toJson(json);
            Files.writeString(configFile, jsonData);
        } catch (Exception e) {
            Mialib.LOGGER.info(e.toString());
        }
    }

    protected static void loadConfig() {
        try {
            var gson = new Gson();
            var reader = Files.readString(configFile);
            var data = gson.fromJson(reader, JsonObject.class);
            for (var values : MVALUES.entrySet()) {
                if (data.has(values.getKey().toString())) {
                    try {
                        values.getValue().readJson(data.get(values.getKey().toString()));
                    } catch (Exception e) {
                        Mialib.LOGGER.info("Error loading mvalue data for {}", values.getKey().toString());
                        Mialib.LOGGER.info(e.toString());
                    }
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