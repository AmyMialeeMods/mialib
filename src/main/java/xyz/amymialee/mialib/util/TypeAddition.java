package xyz.amymialee.mialib.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.amymialee.mialib.interfaces.MGameType;

public record TypeAddition(MinecraftServer server, int code, int[] value) implements GameRules.Visitor {
    public static @Nullable TypeAddition of(MinecraftServer server, int code, int[] value) {
        GameRules.accept(new TypeAddition(server, code, value));
        return null;
    }

    @Override
    public <T extends GameRules.Rule<T>> void visit(GameRules.@NotNull Key<T> key, GameRules.Type<T> type) {
        if (key.getName().hashCode() == this.code) ((MGameType)this.server.getGameRules().get(key)).sync(this.value);
    }
}