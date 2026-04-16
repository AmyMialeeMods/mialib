package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
    public @NotNull MValueMinMax<Long> of(Long defaultValue, Long min, Long max) {
        return new xyz.amymialee.mialib.mvalues.MValueLong(defaultValue, min, max);
    }

    @Override
    public boolean set(@NotNull MValue<Long> mValue, Long value) {
        mValue.value = Math.clamp(value, this.getMin(), this.getMax());
        return true;
    }

    @Override
    public @NotNull CompoundTag writeNbt(@NotNull CompoundTag compound, @NotNull MValue<Long> value) {
        compound.putFloat("value", value.get());
        return compound;
    }

    @Override
    public void readNbt(@NotNull CompoundTag compound, @NotNull MValue<Long> value) {
        value.value = compound.getLongOr("value", this.defaultValue);
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
            CommandRegistrationCallback.EVENT.register((dispatcher, _, _) -> dispatcher.register(Commands.literal("mvalue").requires(Commands.hasPermission(value.permissionCheck))
                    .then(Commands.literal(value.id.toString())
                            .then(Commands.argument("value", LongArgumentType.longArg(minMax.getMin(), minMax.getMax()))
                                    .executes(ctx -> {
                                        value.set(LongArgumentType.getLong(ctx, "value"));
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
