package xyz.amymialee.mialib.mixin.interfaces;

import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import xyz.amymialee.mialib.util.interfaces.MStatusEffect;

@Mixin(StatusEffect.class)
public class StatusEffectMixin implements MStatusEffect {}