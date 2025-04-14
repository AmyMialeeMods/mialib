package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class MValueLong extends MValueMinMax<Long> {
    private final long min;
    private final long max;

    public MValueLong(long defaultValue, long min, long max) {
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull Object getWidget(int x, int y, MValue<Long> value) {
        var self = this;
        return new MValueSliderWidget<>(x, y, value) {
            @Override
            public void resetSliderValue() {
                this.sliderValue = (this.value.get() - self.getMin()) / (self.getMax() - self.getMin() + 0d);
            }

            @Override
            protected Long getValue() {
                return (long) (self.min + (self.getMax() - self.min) * this.sliderValue);
            }
        };
    }

    @Override
    public @NotNull MValueMinMax<Long> of(Long defaultValue, Long min, Long max) {
        return new xyz.amymialee.mialib.mvalues.MValueLong(defaultValue, min, max);
    }

    @Override
    public boolean set(@NotNull MValue<Long> mValue, Long value) {
        mValue.value = Math.clamp(value, this.getMin(), this.getMax());
        return true;
    }

    @Override
    public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Long> value) {
        compound.putFloat("value", value.get());
        return compound;
    }

    @Override
    public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Long> value) {
        value.value = compound.getLong("value", this.defaultValue);
    }

    @Override
    public @NotNull JsonElement writeJson(@NotNull MValue<Long> value) {
        return new JsonPrimitive(value.get());
    }

    @Override
    public void readJson(@NotNull JsonElement json, @NotNull MValue<Long> value) {
        value.set(json.getAsLong());
    }

    @Override
    public Long getMin() {
        return this.min;
    }

    @Override
    public Long getMax() {
        return this.max;
    }

    @Override
    protected void registerServerCommand(@NotNull MValue<Long> value) {
        if (value.type instanceof MValueMinMax<Long> minMax) {
            CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel))
                    .then(CommandManager.literal(value.id.toString())
                            .then(CommandManager.argument("value", LongArgumentType.longArg(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set(LongArgumentType.getLong(ctx, "value"));
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
    protected void registerClientCommand(@NotNull MValue<Long> value) {
        if (value.type instanceof MValueMinMax<Long> minMax) {
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel))
                    .then(ClientCommandManager.literal(value.id.toString())
                            .then(ClientCommandManager.argument("value", LongArgumentType.longArg(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set(LongArgumentType.getLong(ctx, "value"));
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
