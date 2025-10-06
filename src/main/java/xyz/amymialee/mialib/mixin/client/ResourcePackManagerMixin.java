package xyz.amymialee.mialib.mixin.client;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.util.MDir;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"), remap = false)
    private ImmutableSet<ResourcePackProvider> mialib$moreDirs(Object @NotNull [] array, @NotNull Operation<ImmutableSet<ResourcePackProvider>> operation) {
        var universalDir = MDir.getMialibPath("resourcepacks/universal");
        var versionDir = MDir.getMialibPath("resourcepacks/pack_format_" + SharedConstants.getGameVersion().packVersion(ResourceType.CLIENT_RESOURCES));
        var universalProvider = new FileResourcePackProvider(universalDir, ResourceType.CLIENT_RESOURCES, ResourcePackSource.NONE, MinecraftClient.getInstance().getSymlinkFinder());
        var versionProvider = new FileResourcePackProvider(versionDir, ResourceType.CLIENT_RESOURCES, ResourcePackSource.NONE, MinecraftClient.getInstance().getSymlinkFinder());
        var combined = new ResourcePackProvider[array.length + 2];
        System.arraycopy(array, 0, combined, 0, array.length);
        combined[array.length] = universalProvider;
        combined[array.length + 1] = versionProvider;
        return operation.call((Object) combined);
    }
}