package xyz.amymialee.mialib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import xyz.amymialee.mialib.mvalues.MValue;
import xyz.amymialee.mialib.mvalues.MValueManager;
import xyz.amymialee.mialib.mvalues.MValueScreen;

public class MiaLibClient implements ClientModInitializer {
    public static LivingEntity renderingEntityWithItem = null;
    public static ModelTransformationMode currentMode = ModelTransformationMode.NONE;
    private static final KeyBinding keyBindingMValues = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.%s.mvalues".formatted(MiaLib.MOD_ID),
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.%s".formatted(MiaLib.MOD_ID)
    ));

    @Override
    public void onInitializeClient() {
        MRegistry.tryBuildAll("Mialib Client");
        ClientPlayNetworking.registerGlobalReceiver(MiaLib.id("floaty"), (minecraftClient, playNetworkHandler, packetByteBuf, packetSender) -> {
            var stack = packetByteBuf.readItemStack();
            minecraftClient.execute(() -> minecraftClient.gameRenderer.showFloatingItem(stack));
        });
        ClientPlayNetworking.registerGlobalReceiver(MValue.MVALUE_SYNC, (client, playNetworkHandler, buf, packetSender) -> {
            var id = buf.readIdentifier();
            var nbt = buf.readNbt();
            client.execute(() -> {
                var mValue = MValueManager.get(id);
                if (mValue != null) {
                    mValue.readNbt(nbt);
                    if (client.currentScreen instanceof MValueScreen screen) {
                        screen.refreshWidgets();
                    }
                }
            });
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBindingMValues.wasPressed()) {
                client.execute(() -> client.setScreen(new MValueScreen()));
            }
        });
    }

    static {
        for (var mode : ModelTransformationMode.values()) {
            ModelPredicateProviderRegistry.register(MiaLib.id(mode.name().toLowerCase()), (stack, world, entity, seed) -> mode == currentMode ? 1.0F : 0.0F);
        }
    }
}