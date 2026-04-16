package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class MValueEnum<T extends Enum<T>> extends MValueType<T> {
    public final Class<T> type;

    public MValueEnum(@NotNull T defaultValue) {
        this.type = defaultValue.getDeclaringClass();
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean set(@NotNull MValue<T> mValue, T value) {
        mValue.value = value;
        return true;
    }

    @Override
    public @NotNull CompoundTag writeNbt(@NotNull CompoundTag compound, @NotNull MValue<T> value) {
        compound.putString("value", value.get().name());
        return compound;
    }

    @Override
    public void readNbt(@NotNull CompoundTag compound, @NotNull MValue<T> value) {
        value.value = Enum.valueOf(this.type, compound.getStringOr("value", String.valueOf(this.defaultValue)));
    }

    @Override
    public @NotNull JsonElement writeJson(@NotNull MValue<T> value) {
        return new JsonPrimitive(value.get().name());
    }

    @Override
    public void readJson(@NotNull JsonElement json, @NotNull MValue<T> value) {
        value.set(Enum.valueOf(this.type, json.getAsString()));
    }

    @Override
    protected void registerServerCommand(@NotNull MValue<T> value) {
        CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> {
            dispatcher.register(Commands.literal("mvalue").requires(Commands.hasPermission(value.permissionCheck)).then(Commands.literal(value.id.toString()).executes(ctx -> {
                ctx.getSource().sendSuccess(() -> Component.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                return 1;
            })));
            for (var enumConstant : value.type.defaultValue.getDeclaringClass().getEnumConstants()) {
                dispatcher.register(Commands.literal("mvalue").requires(Commands.hasPermission(value.permissionCheck)).then(Commands.literal(value.id.toString())
                        .then(Commands.literal(enumConstant.name())
                                .executes(ctx -> {
                                    value.set(enumConstant);
                                    MVServerManager.INSTANCE.onChange(value);
                                    ctx.getSource().sendSuccess(() -> Component.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                    return 1;
                                }))));
            }
        });
    }
}