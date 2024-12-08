package xyz.amymialee.mialib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface MiaLibEvents {
    Event<DamagePreventionCallback> DAMAGE_PREVENTION = EventFactory.createArrayBacked(DamagePreventionCallback.class, callbacks -> (entity, source) -> {
        for (var callback : callbacks) {
            var result = callback.isInvulnerableTo(entity, source);
            if (result) return true;
        }
        return false;
    });

    @FunctionalInterface interface DamagePreventionCallback {
        boolean isInvulnerableTo(Entity entity, DamageSource source);
    }

    Event<DamageInteractionCallback> DAMAGE_INTERACTION = EventFactory.createArrayBacked(DamageInteractionCallback.class, callbacks -> (entity, source, amount) -> {
        var damage = amount;
        for (var callback : callbacks) {
            var result = callback.modifyDamage(entity, source, damage);
            if (result.isPresent()) {
                damage = result.get();
            } else {
                return Optional.empty();
            }
        }
        return Optional.of(damage);
    });

    @FunctionalInterface interface DamageInteractionCallback {
        Optional<Float> modifyDamage(LivingEntity entity, DamageSource source, float amount);
    }
}