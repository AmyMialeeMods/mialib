package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class MValueBoolean extends MValueType<Boolean> {
    public MValueBoolean(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public @NotNull Object getWidget(int x, int y, MValue<Boolean> value) {
        return new MValueWidget<>(x, y, value) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                if (mouseX < this.getX() + 18) {
                    this.value.send(this.value.type.defaultValue);
                } else {
                    this.value.send(!this.value.get());
                }
            }
        };
    }

    @Override
    public boolean set(@NotNull MValue<Boolean> mValue, Boolean value) {
        mValue.value = value;
        return true;
    }

    @Override
    public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Boolean> value) {
        compound.putBoolean("value", value.get());
        return compound;
    }

    @Override
    public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Boolean> value) {
        value.value = compound.getBoolean("value");
    }

    @Override
    public @NotNull JsonElement writeJson(@NotNull MValue<Boolean> value) {
        return new JsonPrimitive(value.get());
    }

    @Override
    public void readJson(@NotNull JsonElement json, @NotNull MValue<Boolean> value) {
        value.set(json.getAsBoolean());
    }

    @Override
    protected void registerServerCommand(MValue<Boolean> value) {
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel))
                .then(CommandManager.literal(value.id.toString())
                        .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    value.set(BoolArgumentType.getBool(ctx, "enabled"));
                                    MVServerManager.INSTANCE.onChange(value);
                                    ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                    return 1;
                                })
                        ).executes(ctx -> {
                            ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                            return 1;
                        }))));
    }

    @Override
    protected void registerClientCommand(MValue<Boolean> value) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel))
                .then(ClientCommandManager.literal(value.id.toString())
                        .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    value.set(BoolArgumentType.getBool(ctx, "enabled"));
                                    ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
                                    return 1;
                                })
                        ).executes(ctx -> {
                            ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
                            return 1;
                        }))));
    }
}
