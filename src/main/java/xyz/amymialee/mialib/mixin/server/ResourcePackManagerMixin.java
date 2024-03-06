package xyz.amymialee.mialib.mixin.server;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.MinecraftVersion;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.*;
import net.minecraft.util.path.SymlinkFinder;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.config.MiaLibProperties;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"))
    private ImmutableSet<ResourcePackProvider> mialib$moreDirs(Object @NotNull [] array, @NotNull Operation<ImmutableSet<ResourcePackProvider>> operation) {
        var universalDir = MiaLibProperties.getMialibPath("datapacks/universal");
        var versionDir = MiaLibProperties.getMialibPath("datapacks/pack_format_" + MinecraftVersion.CURRENT.getResourceVersion(ResourceType.SERVER_DATA));
        var universalProvider = new FileResourcePackProvider(universalDir, ResourceType.SERVER_DATA, ResourcePackSource.NONE, new SymlinkFinder(path -> true));
        var versionProvider = new FileResourcePackProvider(versionDir, ResourceType.SERVER_DATA, ResourcePackSource.NONE, new SymlinkFinder(path -> true));
        var combined = new ResourcePackProvider[array.length + 2];
        System.arraycopy((ResourcePackProvider[]) array, 0, combined, 0, array.length);
        combined[array.length] = universalProvider;
        combined[array.length + 1] = versionProvider;
        return operation.call((Object) combined);
    }
}