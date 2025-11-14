package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class MValueEnum<T extends Enum<T>> extends MValueType<T> {
    public final Class<T> type;

    public MValueEnum(@NotNull T defaultValue) {
        this.type = defaultValue.getDeclaringClass();
        this.defaultValue = defaultValue;
    }

    @Override
    public @NotNull Object getWidget(int x, int y, MValue<T> value) {
        var constants = this.type.getEnumConstants();
        return new MValueSliderWidget<>(x, y, value) {
            @Override
            public void resetSliderValue() {
                this.sliderValue = (double) (this.value.get().ordinal()) / (constants.length - 1);
            }

            @Override
            protected T getValue() {
                return constants[(int) Math.clamp(constants.length * this.sliderValue, 0, constants.length - 1)];
            }
        };
    }

    @Override
    public boolean set(@NotNull MValue<T> mValue, T value) {
        mValue.value = value;
        return true;
    }

    @Override
    public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<T> value) {
        compound.putString("value", value.get().name());
        return compound;
    }

    @Override
    public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<T> value) {
        value.value = Enum.valueOf(this.type, compound.getString("value", String.valueOf(this.defaultValue)));
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
        CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> {
            dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel)).then(CommandManager.literal(value.id.toString()).executes(ctx -> {
                ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()), false);
                return 1;
            })));
            for (var enumConstant : value.type.defaultValue.getDeclaringClass().getEnumConstants()) {
                dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel)).then(CommandManager.literal(value.id.toString())
                        .then(CommandManager.literal(enumConstant.name())
                                .executes(ctx -> {
                                    value.set(enumConstant);
                                    MVServerManager.INSTANCE.onChange(value);
                                    ctx.getSource().sendFeedback(() -> Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()), true);
                                    return 1;
                                }))));
            }
        });
    }

    @Override
    protected void registerClientCommand(@NotNull MValue<T> value) {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> {
            dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.getPlayer().hasPermissionLevel(value.permissionLevel)).then(ClientCommandManager.literal(value.id.toString()).executes(ctx -> {
                ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.query", value.getText(), value.getValueAsString()));
                return 1;
            })));
            for (var enumConstant : value.type.defaultValue.getDeclaringClass().getEnumConstants()) {
                dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.getPlayer().hasPermissionLevel(value.permissionLevel)).then(ClientCommandManager.literal(value.id.toString())
                        .then(ClientCommandManager.literal(enumConstant.name())
                                .executes(ctx -> {
                                    value.set(enumConstant);
                                    MVServerManager.INSTANCE.onChange(value);
                                    ctx.getSource().sendFeedback(Text.translatable("commands.mvalue.set", value.getText(), value.getValueAsString()));
                                    return 1;
                                }))));
            }
        });
    }
}