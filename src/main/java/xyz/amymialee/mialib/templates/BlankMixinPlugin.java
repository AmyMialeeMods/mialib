package xyz.amymialee.mialib.templates;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin Plugin with all abstract methods given a default body
 * Most of the mixin plugins I see used only actually use one or two of the methods, so why should they need to do them all?
 */
public @SuppressWarnings("unused") interface BlankMixinPlugin extends IMixinConfigPlugin {
    default @Override void onLoad(String mixinPackage) {}

    default @Override String getRefMapperConfig() {
        return null;
    }

    default @Override boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    default @Override void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    default @Override List<String> getMixins() {
        return null;
    }

    default @Override void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    default @Override void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}
}