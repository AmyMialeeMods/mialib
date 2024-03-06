package xyz.amymialee.mialib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.values.MValueManager;

public class MiaLibClient implements ClientModInitializer {
    public static LivingEntity renderingEntityWithItem = null;
    public static ModelTransformationMode currentMode = ModelTransformationMode.NONE;

    @Override
    public void onInitializeClient() {
        MRegistry.tryBuildAll("Mialib Client");
        ClientPlayNetworking.registerGlobalReceiver(MiaLib.id("mvaluesync"), (client, handler, buf, responseSender) -> {
            var nbt = buf.readNbt();
            if (nbt == null) return;
            client.execute(() -> {
                var value = MValueManager.getValues().get(new Identifier(nbt.getString("id")));
                if (value != null) {
                    value.readFromNbt(nbt);
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(MiaLib.id("floaty"), (minecraftClient, playNetworkHandler, packetByteBuf, packetSender) -> {
            var stack = packetByteBuf.readItemStack();
            minecraftClient.execute(() -> minecraftClient.gameRenderer.showFloatingItem(stack));
        });
//        MValueType.BOOLEAN.setDefaultWidgetFactory(MValueBooleanButton::new);
//        MValueType.INTEGER.setDefaultWidgetFactory(MValueSlider::new);
//        MValueType.LONG.setDefaultWidgetFactory(MValueSlider::new);
//        MValueType.FLOAT.setDefaultWidgetFactory(MValueSlider::new);
//        MValueType.DOUBLE.setDefaultWidgetFactory(MValueSlider::new);
    }

    static {
        for (var mode : ModelTransformationMode.values()) {
            ModelPredicateProviderRegistry.register(MiaLib.id(mode.name().toLowerCase()), (stack, world, entity, seed) -> mode == currentMode ? 1.0F : 0.0F);
        }
    }
}