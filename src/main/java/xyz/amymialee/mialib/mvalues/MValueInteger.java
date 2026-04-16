package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
    public @NotNull MValueMinMax<Integer> of(Integer defaultValue, Integer min, Integer max) {
        return new MValueInteger(defaultValue, min, max);
    }

    @Override
    public boolean set(@NotNull MValue<Integer> mValue, Integer value) {
        mValue.value = Math.clamp(value, this.getMin(), this.getMax());
        return true;
    }

    @Override
    public @NotNull CompoundTag writeNbt(@NotNull CompoundTag compound, @NotNull MValue<Integer> value) {
        compound.putInt("value", value.get());
        return compound;
    }

    @Override
    public void readNbt(@NotNull CompoundTag compound, @NotNull MValue<Integer> value) {
        value.value = compound.getIntOr("value", this.defaultValue);
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
            CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> dispatcher.register(Commands.literal("mvalue").requires(Commands.hasPermission(value.permissionCheck))
                    .then(Commands.literal(value.id.toString())
                            .then(Commands.argument("value", IntegerArgumentType.integer(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set(IntegerArgumentType.getInteger(ctx, "value"));
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
}
