package xyz.amymialee.mialib;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.cca.HoldingComponent;
import xyz.amymialee.mialib.cca.IdCooldownComponent;
import xyz.amymialee.mialib.modules.CommandModule;
import xyz.amymialee.mialib.modules.EventModule;
import xyz.amymialee.mialib.modules.ItemModule;
import xyz.amymialee.mialib.modules.NetworkingModule;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueCategory;

public class MiaLib implements ModInitializer, EntityComponentInitializer {
    public static final String MOD_ID = "mialib";
    public static final String MOD_NAME = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getName();
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    // MValues
    public static final MValueCategory MIALIB_CATEGORY = new MValueCategory(id(MOD_ID), Items.DIAMOND.getDefaultStack(), new Identifier("textures/block/purple_concrete.png"));
    public static final MValue.MValueBoolean CREATIVE_NO_SLEEP = MValue.ofBoolean(MIALIB_CATEGORY, id("creative_no_sleep"), Items.BLUE_BED.getDefaultStack(), Items.RED_BED.getDefaultStack(), false);
    public static final MValue.MValueBoolean FIRE_ASPECT_AUTO_SMELT = MValue.ofBoolean(MIALIB_CATEGORY, id("fire_aspect_auto_smelt"), Items.FIRE_CHARGE.getDefaultStack(), Items.COAL.getDefaultStack(), false);
    public static final MValue.MValueBoolean DISABLE_PIGLIN_PORTAL_SPAWNING = MValue.ofBoolean(MIALIB_CATEGORY, id("disable_piglin_portal_spawning"), Items.ROTTEN_FLESH.getDefaultStack(), Items.GOLD_NUGGET.getDefaultStack(), false);
    public static final MValue.MValueBoolean DISABLE_NETHER_PORTALS = MValue.ofBoolean(MIALIB_CATEGORY, id("disable_nether_portals"), Items.OBSIDIAN.getDefaultStack().mialib$enchantStack(Enchantments.FLAME), Items.OBSIDIAN.getDefaultStack(), false);
    public static final MValue.MValueBoolean DISABLE_END_PORTALS = MValue.ofBoolean(MIALIB_CATEGORY, id("disable_end_portals"), Items.END_PORTAL_FRAME.getDefaultStack().mialib$enchantStack(Enchantments.FLAME), Items.END_PORTAL_FRAME.getDefaultStack(), false);

    @Override
    public void onInitialize() {
        CommandModule.init();
        EventModule.init();
        ItemModule.init();
        NetworkingModule.init();
    }

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, IdCooldownComponent.KEY).end(IdCooldownComponent::new);
        registry.beginRegistration(PlayerEntity.class, HoldingComponent.KEY).respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end(HoldingComponent::new);
        registry.beginRegistration(Entity.class, ExtraFlagsComponent.KEY).respawnStrategy(RespawnCopyStrategy.ALWAYS_COPY).end(ExtraFlagsComponent::new);
    }

    public static @NotNull Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}