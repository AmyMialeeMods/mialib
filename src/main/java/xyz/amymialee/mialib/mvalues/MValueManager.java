package xyz.amymialee.mialib.mvalues;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardSyncCallback;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.cca.HoldingComponent;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MValueManager implements Component {
    public static final ComponentKey<MValueManager> KEY = ComponentRegistry.getOrCreate(Mialib.id("mvalues"), MValueManager.class);
    public static final Map<Identifier, MValue<?>> MVALUES = new HashMap<>();
    public static final List<MValueCategory> CATEGORIES = new ArrayList<>();
    public static MValueManager INSTANCE;
    public final MinecraftServer server;

    public MValueManager(MinecraftServer server) {
        if (INSTANCE != null && INSTANCE.server != null) {
            this.server = server;
            return;
        }
        INSTANCE = this;
        this.server = server;
        if (this.isServer()) this.loadConfig();
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

    public <T> void onChangeServer(@NotNull MValue<T> value) {
        if (value.clientSide) return;
        this.saveConfig();
        for (var player : this.server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(player, new MValuePayload(value.id, value.type.writeNbt(new NbtCompound(), value)));
        }
    }

    public <T> void onChangeClient(@NotNull MValue<T> value) {
        if (value.clientSide) {
            this.saveConfig();
        } else {
            ClientPlayNetworking.send(new MValuePayload(value.id, value.type.writeNbt(new NbtCompound(), value)));
        }
    }

    public void saveConfig() {
        for (var isClient : new boolean[]{true, false}) {
            try {
                var gson = new GsonBuilder().setPrettyPrinting().create();
                var json = new JsonObject();
                for (var entry : MVALUES.entrySet()) {
                    if (isClient != entry.getValue().clientSide) continue;
                    json.add(entry.getKey().toString(), entry.getValue().writeJson());
                }
                Files.writeString(this.getPath(isClient), gson.toJson(json));
            } catch (Exception e) {
                Mialib.LOGGER.info(e.toString());
            }
        }
    }

    protected void loadConfig() {
        for (var isClient : new boolean[]{true, false}) {
            try {
                var data = new Gson().fromJson(Files.readString(this.getPath(isClient)), JsonObject.class);
                for (var entry : MVALUES.entrySet()) {
                    if (!data.has(entry.getKey().toString())) continue;
                    if (isClient != entry.getValue().clientSide) continue;
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

    public boolean isClient() {
        return this.server == null || !(this.server instanceof MinecraftDedicatedServer);
    }

    public boolean isServer() {
        return this.server != null;
    }

    public Path getPath(boolean client) {
        return FabricLoader.getInstance().getConfigDir().resolve(client ? "mvaluesclient.json" : "mvalues.json");
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {}

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {}
}