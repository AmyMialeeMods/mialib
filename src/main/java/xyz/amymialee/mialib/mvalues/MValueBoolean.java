package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class MValueBoolean extends MValueType<Boolean> {
    public MValueBoolean(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean set(@NotNull MValue<Boolean> mValue, Boolean value) {
        mValue.value = value;
        return true;
    }

    @Override
    public @NotNull CompoundTag writeNbt(@NotNull CompoundTag compound, @NotNull MValue<Boolean> value) {
        compound.putBoolean("value", value.get());
        return compound;
    }

    @Override
    public void readNbt(@NotNull CompoundTag compound, @NotNull MValue<Boolean> value) {
        value.value = compound.getBooleanOr("value", this.defaultValue);
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
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> dispatcher.register(Commands.literal("mvalue").requires(Commands.hasPermission(value.permissionCheck))
                .then(Commands.literal(value.id.toString())
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                .executes(ctx -> {
                                    value.set(BoolArgumentType.getBool(ctx, "enabled"));
                                    MVServerManager.INSTANCE.onChange(value);
                                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                    return 1;
                                })
                        ).executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                            return 1;
                        }))));
    }
}
