package xyz.amymialee.mialib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.world.GameRules;
import xyz.amymialee.mialib.registration.MRegistry;

public class MiaLibClient implements ClientModInitializer {
    public static ModelTransformationMode currentMode = ModelTransformationMode.NONE;

    @Override
    public void onInitializeClient() {
        MiaLib.LOGGER.info("Building %d MiaLib Registr%s on Client".formatted(MRegistry.REGISTRIES.size(), MRegistry.REGISTRIES.size() == 1 ? "y" : "ies"));
        MRegistry.REGISTRIES.forEach(MRegistry::build);
        ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("mgamerules").executes(context -> {
            var client = context.getSource().getClient();
            client.execute(() -> {
                var world = client.world;
                if (world != null) {
                    var gamerules = world.getGameRules().copy();
                    client.setScreen(new EditGameRulesScreen(gamerules, (newrules -> newrules.ifPresent(gameRules -> new GameRules.Visitor() {
                        @Override
                        public <T extends GameRules.Rule<T>> void visit(GameRules.Key<T> key, GameRules.Type<T> type) {
                            if (gamerules.get(key) != gameRules.get(key)) {
                                var buf = PacketByteBufs.create();
                                buf.writeString(key.getName());
                                buf.writeString(key.getCategory().getCategory());
                                buf.writeString(gameRules.get(key).serialize());
                                ClientPlayNetworking.send(MiaLib.id("gamerule"), buf);
                            }
                        }
                    }))));
                }
            });
            return 0;
        }))));
    }

    static {
        for (var mode : ModelTransformationMode.values()) {
            ModelPredicateProviderRegistry.register(MiaLib.id(mode.name().toLowerCase()), (stack, world, entity, seed) -> mode == currentMode ? 1.0F : 0.0F);
        }
    }
}