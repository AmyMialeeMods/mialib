package xyz.amymialee.mialib.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;

public interface MialibEvents {
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
}