package xyz.amymialee.mialib.mixin.server;

import net.minecraft.resource.ResourcePackManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
//    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableSet;copyOf([Ljava/lang/Object;)Lcom/google/common/collect/ImmutableSet;"))
//    private ImmutableSet<ResourcePackProvider> mialib$moreDirs(Object @NotNull [] array, @NotNull Operation<ImmutableSet<ResourcePackProvider>> operation) {
//        var universalDir = MDir.getMialibPath("datapacks/universal");
//        var versionDir = MDir.getMialibPath("datapacks/pack_format_" + MinecraftVersion.CURRENT.getResourceVersion(ResourceType.SERVER_DATA));
//        var universalProvider = new FileResourcePackProvider(universalDir, ResourceType.SERVER_DATA, ResourcePackSource.NONE, new SymlinkFinder(path -> true));
//        var versionProvider = new FileResourcePackProvider(versionDir, ResourceType.SERVER_DATA, ResourcePackSource.NONE, new SymlinkFinder(path -> true));
//        var combined = new ResourcePackProvider[array.length + 2];
//        System.arraycopy((ResourcePackProvider[]) array, 0, combined, 0, array.length);
//        combined[array.length] = universalProvider;
//        combined[array.length + 1] = versionProvider;
//        return operation.call((Object) combined);
//    }
}