package xyz.amymialee.mialib.templates;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

@SuppressWarnings("unused")
public class MStatusEffect extends StatusEffect {
    public MStatusEffect(StatusEffectCategory statusEffectCategory, int color) {
        super(statusEffectCategory, color);
    }
}