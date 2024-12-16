package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class MValueInteger extends MValueMinMax<Integer> {
    public final int min;
    public final int max;

    public MValueInteger(int defaultValue, int min, int max) {
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull Object getWidget(int x, int y, MValue<Integer> value) {
        var self = this;
        return new MValueSliderWidget<>(x, y, value) {
            @Override
            public void resetSliderValue() {
                this.sliderValue = (this.value.get() - self.getMin()) / (self.getMax() - self.getMin() + 0d);
            }

            @Override
            protected Integer getValue() {
                return (int) (self.min + (self.getMax() - self.min) * this.sliderValue);
            }
        };
    }

    @Override
    public @NotNull MValueMinMax<Integer> of(Integer defaultValue, Integer min, Integer max) {
        return new xyz.amymialee.mialib.mvalues.MValueInteger(defaultValue, min, max);
    }

    @Override
    public boolean set(@NotNull MValue<Integer> mValue, Integer value) {
        mValue.value = Math.clamp(value, this.getMin(), this.getMax());
        return true;
    }

    @Override
    public @NotNull NbtCompound writeNbt(@NotNull NbtCompound compound, @NotNull MValue<Integer> value) {
        compound.putInt("value", value.get());
        return compound;
    }

    @Override
    public void readNbt(@NotNull NbtCompound compound, @NotNull MValue<Integer> value) {
        value.value = compound.getInt("value");
    }

    @Override
    public @NotNull JsonElement writeJson(@NotNull MValue<Integer> value) {
        return new JsonPrimitive(value.get());
    }

    @Override
    public void readJson(@NotNull JsonElement json, @NotNull MValue<Integer> value) {
        value.set(json.getAsInt());
    }

    @Override
    public Integer getMin() {
        return this.min;
    }

    @Override
    public Integer getMax() {
        return this.max;
    }

    @Override
    protected void registerServerCommand(@NotNull MValue<Integer> value) {
        if (value.type instanceof MValueMinMax<Integer> minMax) {
            CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) -> dispatcher.register(CommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel))
                    .then(CommandManager.literal(value.id.toString())
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set(IntegerArgumentType.getInteger(ctx, "value"));
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
            ClientCommandRegistrationCallback.EVENT.register((dispatcher, access) -> dispatcher.register(ClientCommandManager.literal("mvalue").requires(source -> source.hasPermissionLevel(value.permissionLevel))
                    .then(ClientCommandManager.literal(value.id.toString())
                            .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set(IntegerArgumentType.getInteger(ctx, "value"));
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
