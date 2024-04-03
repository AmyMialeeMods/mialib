package xyz.amymialee.mialib.modules;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ActionResult;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.cca.ExtraFlagsComponent;
import xyz.amymialee.mialib.events.ExtraFlagEvents;
import xyz.amymialee.mialib.events.MiaLibEvents;

import java.util.Optional;

public interface EventModule {
    static void init() {
        MiaLibEvents.SMELT_BROKEN_BLOCK.register((world, state, pos, blockEntity, entity, stack) -> {
            if (stack.getItem().mialib$shouldSmelt(world, state, pos, blockEntity, entity, stack)) {
                return ActionResult.SUCCESS;
            }
            if (MiaLib.FIRE_ASPECT_AUTO_SMELT.getValue() && entity instanceof LivingEntity living && EnchantmentHelper.getFireAspect(living) > 0) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        MiaLibEvents.DAMAGE_PREVENTION.register((entity, source) -> {
            var component = ExtraFlagsComponent.KEY.get(entity);
            return component.isIndestructible() || (!(entity instanceof LivingEntity) && component.isImmortal());
        });
        MiaLibEvents.DAMAGE_INTERACTION.register((entity, source, amount) -> {
            var component = ExtraFlagsComponent.KEY.get(entity);
            if (component.isImmortal() && amount > entity.getHealth()) {
                return Optional.of(entity.getHealth() - 1.0F);
            }
            return Optional.of(amount);
        });
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((world, entity, killedEntity) -> {
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.getMainHandStack().getItem().mialib$killEntity(world, livingEntity.getMainHandStack(), livingEntity, killedEntity);
            }
        });
        ExtraFlagEvents.SHOULD_BE_IMPERCEPTIBLE.register((world, entity) -> {
            var component = ExtraFlagsComponent.KEY.get(entity);
            if (component.hasImperceptibleCommand()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        ExtraFlagEvents.SHOULD_BE_INDESTRUCTIBLE.register((world, entity) -> {
            var component = ExtraFlagsComponent.KEY.get(entity);
            if (component.hasIndestructibleCommand()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
        ExtraFlagEvents.SHOULD_BE_IMMORTAL.register((world, entity) -> {
            var component = ExtraFlagsComponent.KEY.get(entity);
            if (component.hasImmortalCommand()) {
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}