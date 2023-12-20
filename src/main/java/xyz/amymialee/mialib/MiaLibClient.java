package xyz.amymialee.mialib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.values.MValueManager;

public class MiaLibClient implements ClientModInitializer {
    public static LivingEntity renderingEntityWithItem = null;
    public static ModelTransformationMode currentMode = ModelTransformationMode.NONE;
//    public static KeyBinding keyBindingOpenStore;

    @Override
    public void onInitializeClient() {
        if (!MRegistry.REGISTRIES.isEmpty()) {
            MiaLib.LOGGER.info("Building %d MiaLib Registr%s on Client".formatted(MRegistry.REGISTRIES.size(), MRegistry.REGISTRIES.size() == 1 ? "y" : "ies"));
            MRegistry.REGISTRIES.forEach(MRegistry::build);
        }
//        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("mgamerules").executes(context -> {
//            var client = context.getSource().getClient();
//            client.execute(() -> {
//                var world = client.world;
//                if (world != null) {
//                    var gamerules = world.getGameRules().copy();
//                    client.setScreen(new EditGameRulesScreen(gamerules, (newrules -> newrules.ifPresent(gameRules -> new GameRules.Visitor() {
//                        @Override
//                        public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
//                            if (gamerules.get(key) != gameRules.get(key)) {
//                                var buf = PacketByteBufs.create();
//                                buf.writeString(key.getName());
//                                buf.writeString(key.getCategory().getCategory());
//                                buf.writeString(gameRules.get(key).serialize());
//                                ClientPlayNetworking.send(MiaLib.id("gamerule"), buf);
//                            }
//                        }
//                    }))));
//                }
//            });
//            return 0;
//        }))));
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

//        keyBindingOpenStore = KeyBindingHelper.registerKeyBinding(new KeyBinding(
//                "key.fundyadvertisement.open_store",
//                GLFW.GLFW_KEY_N,
//                KeyBinding.INVENTORY_CATEGORY
//        ));
//        ClientTickEvents.START_CLIENT_TICK.register(client -> {
//                    if (keyBindingOpenStore.wasPressed()) {
//                        client.setScreen(new MValueMenuScreen(Text.literal("MiaLib Values")));
//                    }
//                });


//        MValueType.BOOLEAN.setDefaultWidgetFactory(MValueBooleanButton::new);
//        MValueType.INTEGER.setDefaultWidgetFactory(MValueSlider::new);
//        MValueType.LONG.setDefaultWidgetFactory(MValueSlider::new);
//        MValueType.FLOAT.setDefaultWidgetFactory(MValueSlider::new);
//        MValueType.DOUBLE.setDefaultWidgetFactory(MValueSlider::new);
    }

    static {
        for (var mode : ModelTransformationMode.values()) {
             FabricModelPredicateProviderRegistry.register(MiaLib.id(mode.name().toLowerCase()), (stack, world, entity, seed) -> mode == currentMode ? 1.0F : 0.0F);
        }
    }
}