package xyz.amymialee.mialib;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.amymialee.mialib.cca.IdCooldownComponent;

import java.util.Objects;

public class MiaLib implements ModInitializer, EntityComponentInitializer, ScoreboardComponentInitializer {
    public static final String MOD_ID = "mialib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    // Components
    public static final ComponentKey<IdCooldownComponent> ID_COOLDOWN_COMPONENT = ComponentRegistry.getOrCreate(id("identifier_cooldown"), IdCooldownComponent.class);

    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(id("gamerule"), ((server, player, handler, buf, responseSender) -> {
            if (server.getPermissionLevel(player.getGameProfile()) >= 2) {
                var name = buf.readString();
                var category = buf.readString();
                var value = buf.readString();
                server.execute(() -> new GameRules.Visitor() {
                    @Override
                    public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                        if (Objects.equals(key.getName(), name) && Objects.equals(key.getCategory().getCategory(), category)) {
                            var rule = server.getGameRules().get(key);
                            rule.deserialize(value);
                        }
                    }
                });
            }
        }));
    }

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, ID_COOLDOWN_COMPONENT, IdCooldownComponent::new);
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {

    }

    public static @NotNull Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}