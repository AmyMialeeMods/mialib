package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.ServerList;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.client.MialibServerSpacerWidget;
import xyz.amymialee.mialib.client.MialibServerWidget;

@Mixin(MultiplayerServerListWidget.class)
public class MultiplayerServerListWidgetMixin extends AlwaysSelectedEntryListWidget<MultiplayerServerListWidget.Entry> {
    private @Shadow @Final MultiplayerScreen screen;

    @Unique private ServerList servers;

    public MultiplayerServerListWidgetMixin(MinecraftClient minecraftClient, int i, int j, int k, int l) {
        super(minecraftClient, i, j, k, l);
    }

    @Inject(method = "setServers", at = @At("HEAD"))
    private void mialib$serverlist(ServerList serverList, CallbackInfo ci) {
        this.servers = serverList;
    }

    @WrapOperation(method = "updateEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;addEntry(Lnet/minecraft/client/gui/widget/EntryListWidget$Entry;)I"))
    private int mialib$mialibservers(@NotNull MultiplayerServerListWidget instance, EntryListWidget.Entry<?> entry, @NotNull Operation<Integer> original) {
        instance.addEntry(new MialibServerSpacerWidget());
        if (this.servers != null) this.servers.mialib$getMialibServers().forEach(server -> this.addEntry(new MialibServerWidget(instance, this.screen, server)));
        return original.call(instance, entry);
    }
}