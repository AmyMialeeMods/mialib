package xyz.amymialee.mialib.values;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.MiaLib;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MValues {
	private static final Map<Identifier, MValue<?>> VALUES = new HashMap<>();
	private static boolean frozen = false;

	protected static void register(Identifier id, MValue<?> mValue) {
		if (frozen) {
			var exception = new RuntimeException("MValueManager: Tried to register value %s after config load".formatted(id));
			MiaLib.LOGGER.error("MValueManager: Tried to register value %s after config load".formatted(id), exception);
			throw exception;
		}
		VALUES.put(id, mValue);
	}

	public static int size() {
		return VALUES.size();
	}

	public static boolean isFrozen() {
		return frozen;
	}

	public static void freeze() {
		frozen = true;
	}

	public static void loadConfig() {
		MiaLib.LOGGER.info("Loading %d MValue%s from config".formatted(MValues.size(), MValues.size() == 1 ? "" : "s"));
		var gson = new GsonBuilder().setPrettyPrinting().create();
		try {
			var file = FabricLoader.getInstance().getConfigDir().resolve("mialibvalues.json").toFile();
			if (file.exists()) {
				var json = gson.<JsonObject>fromJson(new JsonReader(new FileReader(FabricLoader.getInstance().getConfigDir().resolve("mialibvalues.json").toFile())), JsonObject.class);
				for (var entry : VALUES.entrySet()) {
					var id = entry.getKey();
					var value = entry.getValue();
					value.readFromJson(json, id);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void saveConfig() {
		MiaLib.LOGGER.info("Saving %d MValue%s to config".formatted(MValues.size(), MValues.size() == 1 ? "" : "s"));
		var gson = new GsonBuilder().setPrettyPrinting().create();
		var json = new JsonObject();
		for (var entry : VALUES.entrySet()) {
			var id = entry.getKey();
			var value = entry.getValue();
			value.addToJson(json, id);
		}
		try {
			gson.toJson(json, new JsonWriter(new FileWriter(FabricLoader.getInstance().getConfigDir().resolve("mialibvalues.json").toFile())));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}