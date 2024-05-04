package xyz.amymialee.mialib;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.templates.BlankMixinPlugin;

public class MialibMixinPlugin implements BlankMixinPlugin {
    @Override
    public boolean shouldApplyMixin(String targetClassName, @NotNull String mixinClassName) {
        if (FabricLoader.getInstance().isModLoaded("connectormod")) {
            if (mixinClassName.endsWith("ResourcePackManagerMixin")) return false;
            if (mixinClassName.endsWith("EnchantmentHelperMixin")) return false;
            if (mixinClassName.endsWith("MultiplayerServerListWidgetMixin")) return false;
        }
        return BlankMixinPlugin.super.shouldApplyMixin(targetClassName, mixinClassName);
    }
}