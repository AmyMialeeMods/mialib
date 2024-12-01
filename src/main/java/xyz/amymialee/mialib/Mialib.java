package xyz.amymialee.mialib;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.cca.HoldingComponent;
import xyz.amymialee.mialib.modules.CommandModule;
import xyz.amymialee.mialib.modules.EventModule;
import xyz.amymialee.mialib.mvalues.*;
import xyz.amymialee.mialib.networking.AttackingPayload;
import xyz.amymialee.mialib.networking.FloatyPayload;
import xyz.amymialee.mialib.networking.UsingPayload;

import java.util.Random;

public @SuppressWarnings("unused") class Mialib implements ModInitializer, EntityComponentInitializer {
    public static final String MOD_ID = "mialib";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getName();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final Random RANDOM = new Random();

    public static final TagKey<Item> UNDESTROYABLE = TagKey.of(Registries.ITEM.getKey(), id("damage_immune"));
    public static final TagKey<Item> UNBREAKABLE = TagKey.of(Registries.ITEM.getKey(), id("unbreakable"));

    public static final MValue<Boolean> DISABLE_PIGLIN_PORTAL_SPAWNING = MValue.of(id("disable_piglin_portal_spawning"), MValue.BOOLEAN_FALSE).item((v) -> v.get() ? Items.ROTTEN_FLESH : Items.GOLD_NUGGET).build();
    public static final MValue<Boolean> DISABLE_END_PORTALS = MValue.of(id("disable_end_portals"), MValue.BOOLEAN_FALSE).item(Items.NETHERITE_SWORD).build();
    public static final MValue<Boolean> UNCAP_MVALUES = MValue.of(id("uncap_mvalues"), MValue.BOOLEAN_FALSE).build();

    static {
        for (var i = 0; i < 32; i++) {
            var cat = new MValueCategory(Mialib.id(Mialib.MOD_ID + "_" + i), Registries.ITEM.getRandom(net.minecraft.util.math.random.Random.create()).get().value(), Identifier.ofVanilla("textures/block/purple_concrete.png"), 16, 16);
            MValue.of(id("a" + "_" + i), MValue.BOOLEAN_FALSE).item(Registries.ITEM.getRandom(net.minecraft.util.math.random.Random.create()).get().value()).category(cat).build();
            MValue.of(id("b" + "_" + i), MValue.BOOLEAN_TRUE).item(Registries.ITEM.getRandom(net.minecraft.util.math.random.Random.create()).get().value()).build();
        }
    }
    public @Override void onInitialize() {
        CommandModule.init();
        EventModule.init();
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