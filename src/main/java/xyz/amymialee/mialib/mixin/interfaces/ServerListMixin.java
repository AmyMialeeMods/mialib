package xyz.amymialee.mialib.mixin.interfaces;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.util.MDir;
import xyz.amymialee.mialib.util.interfaces.MServerList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Mixin(ServerList.class)
public abstract class ServerListMixin implements MServerList {
    private @Unique final List<ServerInfo> mialibServers = new ArrayList<>();
    private @Unique boolean isEditingMialibServer;
    private @Unique ServerInfo editTarget;

    @Shadow @Final private MinecraftClient client;
    @Shadow public abstract ServerInfo get(int index);

    @Override @Unique
    public List<ServerInfo> mialib$getMialibServers() {
        return this.mialibServers;
    }

    @Inject(method = "loadFile", at = @At("RETURN"))
    private void mialib$loadMialibServers(CallbackInfo ci) {
        try {
            this.mialibServers.clear();
            var serverFile = NbtIo.read(MDir.getMialibPath("mialibservers.dat"));
            if (serverFile == null) return;
            var servers = serverFile.getList("servers", NbtElement.COMPOUND_TYPE);
            for (var i = 0; i < servers.size(); i++) this.mialibServers.add(ServerInfo.fromNbt(servers.getCompound(i)));
        } catch (Exception e) {
            Mialib.LOGGER.error("Couldn't load mialib server list", e);
        }
    }

    @Inject(method = "saveFile", at = @At("RETURN"))
    private void mialib$saveMialibServers(CallbackInfo ci) {
        try {
            var serverList = new NbtList();
            for (var i = 0; i < this.mialibServers.size(); i++) {
                var serverInfo = this.mialibServers.get(i);
                if (serverInfo == null) {
                    this.mialibServers.remove(i);
                    i--;
                    continue;
                }
                serverList.add(serverInfo.toNbt());
            }
            var serverCompound = new NbtCompound();
            serverCompound.put("servers", serverList);
            var newFile = File.createTempFile("servers", ".dat", this.client.runDirectory).toPath();
            NbtIo.write(serverCompound, newFile);
            Util.backupAndReplace(MDir.getMialibPath("mialibservers.dat"), newFile, MDir.getMialibPath("mialibservers.dat_old"));
        } catch (Exception e) {
            Mialib.LOGGER.error("Couldn't save mialib server list", e);
        }
    }

    @Inject(method = "get(Ljava/lang/String;)Lnet/minecraft/client/network/ServerInfo;", at = @At("RETURN"), cancellable = true)
    private void mialib$serverbyaddress(String address, @NotNull CallbackInfoReturnable<ServerInfo> cir) {
        if (cir.getReturnValue() != null) return;
        for (var serverInfo : this.mialibServers) {
            if (!serverInfo.address.equals(address)) continue;
            cir.setReturnValue(serverInfo);
        }
    }

    @WrapOperation(method = "remove", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", ordinal = 1))
    private boolean mialib$remove(List<ServerInfo> instance, Object serverInfo, @NotNull Operation<Boolean> original) {
        if (!original.call(instance, serverInfo) && serverInfo instanceof ServerInfo) return this.mialibServers.remove(serverInfo);
        return true;
    }

    @WrapOperation(method = "method_44090", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;replace(Lnet/minecraft/client/network/ServerInfo;Ljava/util/List;)Z", ordinal = 1))
    private static boolean mialib$update(ServerInfo serverInfo, List<ServerInfo> serverInfos, @NotNull Operation<Boolean> original, @Local(ordinal = 0) ServerList serverList) {
        if (!original.call(serverInfo, serverInfos)) return original.call(serverInfo, serverList.mialib$getMialibServers());
        return true;
    }

    public @Override void mialib$addMialibServer(ServerInfo serverInfo) {
        this.mialibServers.add(serverInfo);
    }

    public @Override boolean mialib$isEditingMialibServer() {
        return this.isEditingMialibServer;
    }

    public @Override void mialib$setEditingMialibServer(boolean editing) {
        this.isEditingMialibServer = editing;
    }

    public @Override void mialib$setEditTarget(ServerInfo serverInfo) {
        this.editTarget = serverInfo;
    }

    public @Override ServerInfo mialib$getEditTarget() {
        return this.editTarget;
    }
}