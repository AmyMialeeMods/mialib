package xyz.amymialee.mialib.mvalues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.io.FileNotFoundException;
import java.nio.file.Files;

import net.minecraft.resources.Identifier;

public abstract class MVManager {
    public static MVManager INSTANCE;

    public MVManager() {
        INSTANCE = this;
    }

    public abstract MValue<?> get(Identifier id);

    public abstract <T> void onChange(@NotNull MValue<T> value);

    public static void saveConfig() {
        try {
            var gson = new GsonBuilder().setPrettyPrinting().create();
            var json = new JsonObject();
            for (var entry : MVServerManager.MVALUES.entrySet()) json.add(entry.getKey().toString(), entry.getValue().writeJson());
            Files.writeString(MVServerManager.PATH, gson.toJson(json));
        } catch (Exception e) {
            Mialib.LOGGER.info(e.toString());
        }
    }

    public static void loadConfig() {
        try {
            var data = new Gson().fromJson(Files.readString(MVServerManager.PATH), JsonObject.class);
            for (var entry : MVServerManager.MVALUES.entrySet()) {
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