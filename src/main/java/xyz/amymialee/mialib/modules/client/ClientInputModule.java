package xyz.amymialee.mialib.modules.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import xyz.amymialee.mialib.MiaLib;
import xyz.amymialee.mialib.mvalues.MValueScreen;

public interface ClientInputModule {
     KeyBinding keyBindingMValues = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.%s.mvalues".formatted(MiaLib.MOD_ID),
            InputUtil.UNKNOWN_KEY.getCode(),
            "category.%s".formatted(MiaLib.MOD_ID)
    ));

    static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (keyBindingMValues.wasPressed()) {
                client.execute(() -> client.setScreen(new MValueScreen()));
            }
        });
    }
}