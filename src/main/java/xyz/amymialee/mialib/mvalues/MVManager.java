package xyz.amymialee.mialib.mvalues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public abstract class MVManager {
    public static MVManager INSTANCE;

    public MVManager() {
        INSTANCE = this;
    }

    public abstract MValue<?> get(Identifier id);

    public abstract <T> void onChange(@NotNull MValue<T> value);

    public abstract void saveConfig();

    protected abstract void loadConfig();

    protected static void saveConfig(Map<Identifier, MValue<?>> mvalues, Path path) {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            var json = new JsonObject();
            for (var entry : mvalues.entrySet()) json.add(entry.getKey().toString(), entry.getValue().writeJson());
            Files.writeString(path, gson.toJson(json));
        } catch (Exception e) {
            Mialib.LOGGER.info(e.toString());
        }
    }

    protected static void loadConfig(Path path, Map<Identifier, MValue<?>> mvalues) {
        try {
            var data = new Gson().fromJson(Files.readString(path), JsonObject.class);
            for (var entry : mvalues.entrySet()) {
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