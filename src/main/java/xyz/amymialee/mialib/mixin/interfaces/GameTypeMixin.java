package xyz.amymialee.mialib.mixin.interfaces;

import net.fabricmc.fabric.api.gamerule.v1.rule.DoubleRule;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.amymialee.mialib.interfaces.MGameType;

@Mixin(GameRules.Rule.class)
public abstract class GameTypeMixin implements MGameType {
    @Shadow protected abstract void changed(@Nullable MinecraftServer server);
    @Mixin(GameRules.BooleanRule.class)
    private abstract static class BooleanType extends GameTypeMixin {
        @Shadow private boolean value;
        public void sync(MinecraftServer server, int @NotNull [] data) {
            if (data.length > 0) {
                this.value = (data[0] & 0x1) == 1;
                this.changed(server);
            }
        }
    }
    @Mixin(GameRules.IntRule.class)
    private abstract static class IntType extends GameTypeMixin {
        @Shadow private int value;
        public void sync(MinecraftServer server, int @NotNull [] data) {
            if (data.length > 0) {
                this.value = data[0];
                this.changed(server);
            }
        }
    }
    @Mixin(DoubleRule.class)
    private abstract static class DoubleType extends GameTypeMixin {
        @Shadow private double value;
        public void sync(MinecraftServer server, int @NotNull [] data) {
            if (data.length > 1) {
                this.value = Double.longBitsToDouble(((long) data[0] << 32) | (data[1] & 0xFFFFFFFFL));
                this.changed(server);
            }
        }
    }
}