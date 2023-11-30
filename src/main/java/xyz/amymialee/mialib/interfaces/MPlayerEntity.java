package xyz.amymialee.mialib.interfaces;

import net.minecraft.util.Identifier;

public interface MPlayerEntity {
    boolean mialib$isCoolingDown(Identifier id);
    void mialib$setCooldown(Identifier id, int ticks);
    int mialib$getCooldown(Identifier id);
}