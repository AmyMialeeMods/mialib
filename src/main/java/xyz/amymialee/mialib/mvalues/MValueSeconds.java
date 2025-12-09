package xyz.amymialee.mialib.mvalues;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class MValueSeconds extends MValueInteger {
    public MValueSeconds(float defaultValue, float min, float max) {
        super((int) (defaultValue * 20), (int) (min * 20), (int) (max * 20));
    }

    @Override
    public @NotNull String getValueAsString(@NotNull MValue<Integer> value) {
        return "%.02f".formatted(value.get() / 20f);
    }

    @Override
    public @NotNull MValueMinMax<Integer> of(Integer defaultValue, Integer min, Integer max) {
        return new MValueSeconds(defaultValue, min, max);
    }

    @Override
    protected void registerServerCommand(@NotNull MValue<Integer> value) {
        if (value.type instanceof MValueMinMax<Integer> minMax) {
            CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(CommandManager.requirePermissionLevel(value.permissionCheck))
                    .then(CommandManager.literal(value.id.toString())
                            .then(CommandManager.argument("value", FloatArgumentType.floatArg(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set((int) (FloatArgumentType.getFloat(ctx, "value") * 20));
                                        MVServerManager.INSTANCE.onChange(value);
                                        ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                        return 1;
                                    })
                            ).executes(ctx -> {
                                ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                                return 1;
                            }))));
        }
    }

    @Override
    protected void registerClientCommand(@NotNull MValue<Integer> value) {
        if (value.type instanceof MValueMinMax<Integer> minMax) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(CommandManager.requirePermissionLevel(value.permissionCheck))
                    .then(ClientCommandManager.literal(value.id.toString())
                            .then(ClientCommandManager.argument("value", FloatArgumentType.floatArg(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set((int) (FloatArgumentType.getFloat(ctx, "value") * 20));
                                        ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
                                        return 1;
                                    })
                            ).executes(ctx -> {
                                ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
                                return 1;
                            }))));
        }
    }
}
