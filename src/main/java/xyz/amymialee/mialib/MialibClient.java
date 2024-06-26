package xyz.amymialee.mialib;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import xyz.amymialee.mialib.modules.client.ClientInputModule;
import xyz.amymialee.mialib.modules.client.NetworkingClientModule;
import xyz.amymialee.mialib.templates.MRegistry;

@SuppressWarnings("unused")
public class MialibClient implements ClientModInitializer {
    public static LivingEntity renderingEntityWithItem = null;
    public static ModelTransformationMode currentMode = ModelTransformationMode.NONE;

    @Override
    public void onInitializeClient() {
        MRegistry.tryBuildAll("%s Client".formatted(Mialib.MOD_NAME));
        ClientInputModule.init();
        NetworkingClientModule.init();
    }

    static {
        for (var mode : ModelTransformationMode.values()) {
            ModelPredicateProviderRegistry.register(Mialib.id(mode.name().toLowerCase()), (stack, world, entity, seed) -> mode == currentMode ? 1.0F : 0.0F);
        }
    }
}