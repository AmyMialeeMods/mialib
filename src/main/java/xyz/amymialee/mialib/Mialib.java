package xyz.amymialee.mialib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.cca.HoldingComponent;
import xyz.amymialee.mialib.events.MialibEvents;
import xyz.amymialee.mialib.modules.ExtrasModule;
import xyz.amymialee.mialib.mvalues.MValuePayload;
import xyz.amymialee.mialib.networking.AttackingPayload;
import xyz.amymialee.mialib.networking.FloatyPayload;
import xyz.amymialee.mialib.networking.UsingPayload;
import xyz.amymialee.mialib.util.interfaces.MEnchantment;

import java.util.Random;
import java.util.function.Consumer;

public @SuppressWarnings("unused") class Mialib implements ModInitializer, EntityComponentInitializer {
    public static final String MOD_ID = "mialib";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getName();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    public @Override void onInitialize() {
        ExtrasModule.init();
        MialibEvents.DAMAGE_PREVENTION.register((entity, source) -> {
            var component = ExtraFlagsComponent.KEY.get(entity);
            return component.isIndestructible() || (!(entity instanceof LivingEntity) && component.isImmortal());
        });
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof LivingEntity livingEntity) livingEntity.getMainHandStack().getItem().mialib$killEntity(world, livingEntity.getMainHandStack(), livingEntity, killedEntity);
        });
        Consumer<MinecraftServer> consumer = server -> {
            var registry = server.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);
            //noinspection RedundantCast
            registry.forEach((a) -> ((MEnchantment) a).mialib$setId(registry.getId(a)));
        };
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, serverResourceManager, success) -> consumer.accept(server));
        ServerLifecycleEvents.SERVER_STARTED.register(consumer::accept);
        PayloadTypeRegistry.playC2S().register(AttackingPayload.ID, AttackingPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(UsingPayload.ID, UsingPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(MValuePayload.ID, MValuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(MValuePayload.ID, MValuePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FloatyPayload.ID, FloatyPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(AttackingPayload.ID, new AttackingPayload.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(UsingPayload.ID, new UsingPayload.Receiver());
        ServerPlayNetworking.registerGlobalReceiver(MValuePayload.ID, new MValuePayload.ServerReceiver());
    }
    
    public @Override void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, HoldingComponent.KEY).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(HoldingComponent::new);
        registry.beginRegistration(Entity.class, ExtraFlagsComponent.KEY).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(ExtraFlagsComponent::new);
    }

    public static @NotNull Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}