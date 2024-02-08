package xyz.amymialee.mialib.mixin.interfaces;

import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.amymialee.mialib.interfaces.MGameType;

@Mixin(GameRules.Rule.class)
public class GameTypeMixin implements MGameType {
    @Mixin(GameRules.BooleanRule.class)
    private static class BooleanType extends GameTypeMixin {
        @Shadow private boolean value;
        public void sync(int @NotNull [] data) {
            if (data.length > 0) {
                this.value = (data[0] & 0x1) == 1;
            }
        }
    }
    @Mixin(GameRules.IntRule.class)
    private static class IntType extends GameTypeMixin {
        @Shadow private int value;
        public void sync(int @NotNull [] data) {
            if (data.length > 0) {
                this.value = data[0];
            }
        }
    }
    @Mixin(DoubleRule.class)
    private static class DoubleType extends GameTypeMixin {
        @Shadow private double value;
        public void sync(int @NotNull [] data) {
            if (data.length > 1) this.value = Double.longBitsToDouble(((long) data[0] << 32) | (data[1] & 0xFFFFFFFFL));
        }
    }
}