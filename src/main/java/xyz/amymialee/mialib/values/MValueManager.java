package xyz.amymialee.mialib.values;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialib.MiaLib;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MValueManager implements AutoSyncedComponent {
	private static final Map<Identifier, MValue<?>> VALUES = new HashMap<>();
	private static boolean frozen = false;
	private final Scoreboard scoreboard;
	private final @Nullable MinecraftServer server;

	public MValueManager(Scoreboard scoreboard, @Nullable MinecraftServer server) {
		this.scoreboard = scoreboard;
		this.server = server;
	}

	@Override
	public void readFromNbt(NbtCompound tag) {

	}

	@Override
	public void writeToNbt(NbtCompound tag) {

	}

	protected static void register(Identifier id, MValue<?> mValue) {
		if (frozen) {
			var exception = new RuntimeException("MValueManager: Tried to register value %s after config load".formatted(id));
			MiaLib.LOGGER.error("MValueManager: Tried to register value %s after config load".formatted(id), exception);
			throw exception;
		}
		VALUES.put(id, mValue);
	}

	protected static Map<Identifier, MValue<?>> getValues() {
		return VALUES;
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
		MiaLib.LOGGER.info("Loading %d MValue%s from config".formatted(MValueManager.size(), MValueManager.size() == 1 ? "" : "s"));
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
		MiaLib.LOGGER.info("Saving %d MValue%s to config".formatted(MValueManager.size(), MValueManager.size() == 1 ? "" : "s"));
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

	static {
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			MValueManager.loadConfig();
			MValueManager.freeze();
		});
		ServerLifecycleEvents.SERVER_STOPPING.register((server) -> {
			MValueManager.saveConfig();
		});
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
			for (var entry : VALUES.entrySet()) {
				entry.getValue().updateServerToClient(player, entry.getKey());
			}
		});
		ClientPlayNetworking.registerGlobalReceiver(MiaLib.id("mvaluesync"), (client, handler, buf, responseSender) -> {
			var nbt = buf.readNbt();
			if (nbt == null) return;
			var value = VALUES.get(new Identifier(nbt.getString("id")));
			if (value != null) {
				value.readFromNbt(nbt);
			}
		});
	}
}