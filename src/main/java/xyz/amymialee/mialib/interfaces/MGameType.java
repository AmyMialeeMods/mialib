package xyz.amymialee.mialib.interfaces;

import net.minecraft.server.MinecraftServer;

public interface MGameType {
    default void sync(MinecraftServer server, int[] data) {}
}