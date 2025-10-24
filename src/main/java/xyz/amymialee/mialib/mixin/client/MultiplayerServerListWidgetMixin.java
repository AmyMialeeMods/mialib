package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.option.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.client.MialibServerSpacerWidget;
import xyz.amymialee.mialib.client.MialibServerWidget;

import java.util.Collection;
import java.util.List;

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

    @WrapOperation(method = "updateEntries", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/multiplayer/MultiplayerServerListWidget;replaceEntries(Ljava/util/Collection;)V")) @SuppressWarnings("unchecked")
    private <E extends EntryListWidget.Entry<E>> void mialib$mialibservers(MultiplayerServerListWidget instance, Collection<E> collection, Operation<Void> original) {
        ((List<MultiplayerServerListWidget.Entry>) collection).add(new MialibServerSpacerWidget());
        if (this.servers != null) this.servers.mialib$getMialibServers().forEach(server -> ((List<MultiplayerServerListWidget.Entry>) collection).add(new MialibServerWidget(instance, this.screen, server)));
        original.call(instance, collection);
    }
}