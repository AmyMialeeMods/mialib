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
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.amymialee.mialib.cca.IdCooldownComponent;
import xyz.amymialee.mialib.values.MDoubleValue;
import xyz.amymialee.mialib.values.MFloatValue;
import xyz.amymialee.mialib.values.MIntegerValue;
import xyz.amymialee.mialib.values.MLongValue;
import xyz.amymialee.mialib.values.MValue;
import xyz.amymialee.mialib.values.MValueManager;
import xyz.amymialee.mialib.values.MValueType;

import java.util.Objects;

public class MiaLib implements ModInitializer, EntityComponentInitializer, ScoreboardComponentInitializer {
    public static final String MOD_ID = "mialib";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    // Components
    public static final ComponentKey<IdCooldownComponent> ID_COOLDOWN_COMPONENT = ComponentRegistry.getOrCreate(id("identifier_cooldown"), IdCooldownComponent.class);
    // Scoreboard Components
    public static final ComponentKey<MValueManager> MVALUE_MANAGER = ComponentRegistry.getOrCreate(id("mvalue_manager"), MValueManager.class);

    public static final MValue<Boolean> TEST_BOOLEAN = new MValue<>(id("test_boolean"), (b) -> b ? Items.COOKED_SALMON.getDefaultStack() : Items.SALMON.getDefaultStack(), MValueType.BOOLEAN, false);
    public static final MIntegerValue TEST_INTEGER = new MIntegerValue(id("test_integer"), (i) -> Items.SALMON.getDefaultStack(), 0, 0, 100);
    public static final MLongValue TEST_LONG = new MLongValue(id("test_long"), (l) -> Items.SALMON.getDefaultStack(), 0L, 0L, 100L);
    public static final MFloatValue TEST_FLOAT = new MFloatValue(id("test_float"), (f) -> Items.SALMON.getDefaultStack(), 0.0F, 0.0F, 100.0F);
    public static final MDoubleValue TEST_DOUBLE = new MDoubleValue(id("test_double"), (d) -> Items.SALMON.getDefaultStack(), 0.0D, 0.0D, 100.0D);

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
        ServerPlayNetworking.registerGlobalReceiver(MiaLib.id("mvaluesync"), (server, player, handler, buf, responseSender) -> {
            var nbt = buf.readNbt();
            if (nbt == null) return;
            server.execute(() -> {
                if (player.hasPermissionLevel(2)) {
                    var value = MValueManager.getValues().get(new Identifier(nbt.getString("id")));
                    if (value != null) {
                        value.readFromNbt(nbt);
                        System.out.println("RECEIVED " + value.getValue() + " FROM CLIENT");
                    }
                }
            });
        });
    }

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerFor(PlayerEntity.class, ID_COOLDOWN_COMPONENT, IdCooldownComponent::new);
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(MVALUE_MANAGER, MValueManager::new);

    }

    public static @NotNull Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}