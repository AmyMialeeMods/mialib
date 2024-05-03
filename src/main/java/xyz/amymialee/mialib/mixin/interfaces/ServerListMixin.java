package xyz.amymialee.mialib.mixin.interfaces;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
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
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.util.MDir;
import xyz.amymialee.mialib.util.interfaces.MServerList;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Mixin(ServerList.class)
public abstract class ServerListMixin implements MServerList {
    @Unique private final List<ServerInfo> mialibServers = new ArrayList<>();
    @Unique private boolean isEditingMialibServer = false;
    @Unique private ServerInfo editTarget;

    @Shadow public abstract void saveFile();
    @Shadow @Final public List<ServerInfo> servers;

    @Shadow public abstract ServerInfo get(int index);

    @Unique @Override
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
            for (var i = 0; i < servers.size(); i++) {
                this.mialibServers.add(ServerInfo.fromNbt(servers.getCompound(i)));
            }
        } catch (Exception e) {
            MiaLib.LOGGER.error("Couldn't load mialib server list", e);
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
            var newFile = Files.createTempFile(MDir.getMialibPath(""), "mialibservers", ".dat");
            NbtIo.write(serverCompound, newFile);
            Util.backupAndReplace(MDir.getMialibPath("mialibservers.dat"), newFile, MDir.getMialibPath("mialibservers.dat_old"));
        } catch (Exception e) {
            MiaLib.LOGGER.error("Couldn't save mialib server list", e);
        }
    }

    @Inject(method = "get(I)Lnet/minecraft/client/network/ServerInfo;", at = @At("HEAD"), cancellable = true)
    public void mialib$getMialibServer(int index, CallbackInfoReturnable<ServerInfo> cir) {
        if (index >= this.servers.size()) cir.setReturnValue(this.mialibServers.get(index - this.servers.size()));
    }



    @Inject(method = "get(Ljava/lang/String;)Lnet/minecraft/client/network/ServerInfo;", at = @At("RETURN"), cancellable = true)
    private void mialib$getMialibServerByAddress(String address, @NotNull CallbackInfoReturnable<ServerInfo> cir) {
        if (cir.getReturnValue() == null) {
            for (var serverInfo : this.mialibServers) {
                if (serverInfo.address.equals(address)) {
                    cir.setReturnValue(serverInfo);
                }
            }
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @WrapOperation(method = "remove", at = @At(value = "INVOKE", target = "Ljava/util/List;remove(Ljava/lang/Object;)Z", ordinal = 1))
    private boolean mialib$removeMialibServer(List<ServerInfo> instance, Object serverInfo, @NotNull Operation<Boolean> original) {
        if (!original.call(instance, serverInfo)) {
            return this.mialibServers.remove(serverInfo);
        }
        return true;
    }

    @Unique
    public void mialib$addMialibServer(ServerInfo serverInfo) {
        this.mialibServers.add(serverInfo);
    }

    @Inject(method = "size", at = @At("RETURN"), cancellable = true)
    public void mialib$serverCount(@NotNull CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + this.mialibServers.size());
    }

    @Inject(method = "swapEntries", at = @At("HEAD"), cancellable = true)
    public void mialib$swapMialibServerEntries(int index1, int index2, CallbackInfo ci) {
        var isEitherSeparator = index1 == this.servers.size() || index2 == this.servers.size();
        if (isEitherSeparator) {
            ci.cancel();
        }
        var isMialib1 = index1 > this.servers.size();
        var isMialib2 = index2 > this.servers.size();
        if (isMialib1 && isMialib2) {
            var serverInfo = this.mialibServers.get(index1 - 1 - this.servers.size());
            this.mialibServers.set(index1 - 1 - this.servers.size(), this.mialibServers.get(index2 - 1 - this.servers.size()));
            this.mialibServers.set(index2 - 1 - this.servers.size(), serverInfo);
            this.saveFile();
            ci.cancel();
        } else if (isMialib1 != isMialib2) {
            ci.cancel();
        }
    }

    @Inject(method = "set", at = @At("HEAD"), cancellable = true)
    public void mialib$setMialibServer(int index, ServerInfo serverInfo, CallbackInfo ci) {
        if (index >= this.servers.size()) {
            this.mialibServers.set(index - this.servers.size(), serverInfo);
            ci.cancel();
        }
    }

    @WrapOperation(method = "method_44090", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/ServerList;replace(Lnet/minecraft/client/network/ServerInfo;Ljava/util/List;)Z", ordinal = 1))
    private static boolean mialib$updateMialibServers(ServerInfo serverInfo, List<ServerInfo> serverInfos, @NotNull Operation<Boolean> original, @Local(ordinal = 0) ServerList serverList) {
        if (!original.call(serverInfo, serverInfos)) {
            return original.call(serverInfo, serverList.mialib$getMialibServers());
        }
        return true;
    }

    @Override
    public boolean mialib$isEditingMialibServer() {
        return this.isEditingMialibServer;
    }

    @Override
    public void mialib$setEditingMialibServer(boolean editing) {
        this.isEditingMialibServer = editing;
    }

    @Override
    public void mialib$setEditTarget(ServerInfo serverInfo) {
        this.editTarget = serverInfo;
    }

    @Override
    public ServerInfo mialib$getEditTarget() {
        return this.editTarget;
    }
}