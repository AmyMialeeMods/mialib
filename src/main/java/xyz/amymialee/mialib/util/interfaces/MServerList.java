package xyz.amymialee.mialib.util.interfaces;

import net.minecraft.client.network.ServerInfo;

import java.util.List;

public interface MServerList {
    default List<ServerInfo> mialib$getMialibServers() {
        return null;
    }
    default ServerInfo mialib$getMialibServer(int index) {
        return null;
    }
    default void mialib$addMialibServer(ServerInfo serverInfo) {}
    default int mialib$serverCount() {
        return 0;
    }
    default void mialib$swapMialibServerEntries(int index1, int index2) {}
    default void mialib$setMialibServer(int index, ServerInfo serverInfo) {}
    default boolean mialib$isEditingMialibServer() {
        return false;
    }
    default void mialib$setEditingMialibServer(boolean editing) {}
    default ServerInfo mialib$getEditTarget() {
        return null;
    }
    default void mialib$setEditTarget(ServerInfo serverInfo) {}
}