package xyz.amymialee.mialib.values;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MValueManager implements AutoSyncedComponent {
    private static final Map<Identifier, MValue<?>> VALUES = new HashMap<>();
    private static final Map<String, Set<Map.Entry<Identifier, MValue<?>>>> VALUES_BY_NAMESPACE = new HashMap<>();
    private static boolean frozen = false;
    public static MValueManager SERVER_INSTANCE;
    private final Scoreboard scoreboard;
    protected final @Nullable MinecraftServer server;

    public MValueManager(Scoreboard scoreboard, @Nullable MinecraftServer server) {
        this.scoreboard = scoreboard;
        this.server = server;
        if (this.server != null) SERVER_INSTANCE = this;
    }

    public void sync() {
        MiaLib.MVALUE_MANAGER.sync(this.scoreboard);
    }

    protected static void register(Identifier id, MValue<?> mValue) {
        if (frozen) {
            var exception = new RuntimeException("MValueManager: Tried to register value %s after config load".formatted(id));
            MiaLib.LOGGER.error("MValueManager: Tried to register value %s after config load".formatted(id), exception);
            throw exception;
        }
        VALUES.put(id, mValue);
        if (!VALUES_BY_NAMESPACE.containsKey(id.getNamespace())) VALUES_BY_NAMESPACE.put(id.getNamespace(), new HashSet<>());
        VALUES_BY_NAMESPACE.get(id.getNamespace()).add(Map.entry(id, mValue));
    }

    public void updateServerToClient(MValue<?> value) {
        if (this.server == null) return;
        var buf = PacketByteBufs.create();
        var nbt = new NbtCompound();
        nbt.putString("id", String.valueOf(value.id));
        this.writeToNbt(nbt);
        buf.writeNbt(nbt);
        this.server.getPlayerManager().getPlayerList().forEach((player) -> ServerPlayNetworking.send(player, MiaLib.id("mvaluesync"), buf));
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (!frozen) frozen = true;
        for (var entry : VALUES.entrySet()) {
            var id = entry.getKey();
            var nbt = tag.getCompound(id.toString());
            var value = entry.getValue();
            value.readFromNbt(nbt);
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        for (var entry : VALUES.entrySet()) {
            var id = entry.getKey();
            var value = entry.getValue();
            var nbt = value.addToNbt(new NbtCompound());
            tag.put(id.toString(), nbt);
        }
    }

    public static boolean isFrozen() {
        return frozen;
    }

    public static Map<Identifier, MValue<?>> getValues() {
        return VALUES;
    }

    public static Set<String> getNamespaces() {
        return VALUES_BY_NAMESPACE.keySet();
    }

    public static Set<Map.Entry<Identifier, MValue<?>>> getValuesByNamespace(String namespace) {
        return VALUES_BY_NAMESPACE.get(namespace);
    }

    public static void importConfig() {
        MiaLib.LOGGER.info("Loading %d MValue%s from config".formatted(VALUES.size(), VALUES.size() == 1 ? "" : "s"));
        var gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            var file = FabricLoader.getInstance().getConfigDir().resolve("mialibvalues.json").toFile();
            if (file.exists()) {
                var json = gson.<JsonObject>fromJson(new JsonReader(new FileReader(FabricLoader.getInstance().getConfigDir().resolve("mialibvalues.json").toFile())), JsonObject.class);
                VALUES.values().forEach((v) -> v.readFromJson(json));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void exportConfig() {
        MiaLib.LOGGER.info("Saving %d MValue%s to config".formatted(VALUES.size(), VALUES.size() == 1 ? "" : "s"));
        var gson = new GsonBuilder().setPrettyPrinting().create();
        var json = new JsonObject();
        VALUES.values().forEach((v) -> v.addToJson(json));
        try {
            gson.toJson(json, new JsonWriter(new FileWriter(FabricLoader.getInstance().getConfigDir().resolve("mialibvalues.json").toFile())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}