package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.client.network.ServerInfo;

import java.util.List;

public interface MServerList {
    default List<ServerInfo> mialib$getMialibServers() {
        return null;
    }
    default void mialib$addMialibServer(ServerInfo serverInfo) {}
    default boolean mialib$isEditingMialibServer() {
        return false;
    }
    default void mialib$setEditingMialibServer(boolean editing) {}
    default ServerInfo mialib$getEditTarget() {
        return null;
    }
    default void mialib$setEditTarget(ServerInfo serverInfo) {}
}