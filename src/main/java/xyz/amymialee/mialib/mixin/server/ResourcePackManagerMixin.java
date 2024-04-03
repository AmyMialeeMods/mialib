package xyz.amymialee.mialib.mixin.server;

import com.google.common.collect.ImmutableSet;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.MinecraftVersion;
import net.minecraft.resource.*;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.util.MDir;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"))
    private ImmutableSet<ResourcePackProvider> mialib$moreDirs(Object @NotNull [] array, @NotNull Operation<ImmutableSet<ResourcePackProvider>> operation) {
        var universalDir = MDir.getMialibPath("datapacks/universal");
        var versionDir = MDir.getMialibPath("datapacks/pack_format_" + MinecraftVersion.CURRENT.getResourceVersion(ResourceType.SERVER_DATA));
        var universalProvider = new FileResourcePackProvider(universalDir, ResourceType.SERVER_DATA, ResourcePackSource.NONE);//, new SymlinkFinder(path -> true));
        var versionProvider = new FileResourcePackProvider(versionDir, ResourceType.SERVER_DATA, ResourcePackSource.NONE);//, new SymlinkFinder(path -> true));
        var combined = new ResourcePackProvider[array.length + 2];
        System.arraycopy(array, 0, combined, 0, array.length);
        combined[array.length] = universalProvider;
        combined[array.length + 1] = versionProvider;
        return operation.call((Object) combined);
    }
}