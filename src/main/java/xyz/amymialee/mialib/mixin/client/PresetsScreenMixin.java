package xyz.amymialee.mialib.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screen.PresetsScreen;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PresetsScreen.class)
public class PresetsScreenMixin {
    @Mixin(targets = "net/minecraft/client/gui/screen/PresetsScreen$SuperflatPresetsListWidget")
    static abstract class SuperflatPresetsListWidgetMixin {
        @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/registry/Registry;iterateEntries(Lnet/minecraft/registry/tag/TagKey;)Ljava/lang/Iterable;"))
        private static Iterable<?> iterateEntries(@NotNull Registry<FlatLevelGeneratorPreset> instance, TagKey<FlatLevelGeneratorPreset> tag, Operation<Iterable<RegistryEntry<FlatLevelGeneratorPreset>>> original) {
            return instance.getIndexedEntries();
        }
    }
}