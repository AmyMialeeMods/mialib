package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public abstract class MValueType<T> {
    protected T defaultValue;

    public abstract boolean set(MValue<T> mValue, T value);

    public String getValueAsString(@NotNull MValue<T> value) {
        return String.valueOf(value.get());
    }

    public abstract CompoundTag writeNbt(CompoundTag compound, MValue<T> value);

    public abstract void readNbt(CompoundTag compound, MValue<T> value);

    public abstract JsonElement writeJson(MValue<T> value);

    public abstract void readJson(JsonElement json, MValue<T> value);

    public void registerCommand(@NotNull MValue<T> value) {
        this.registerServerCommand(value);
    }

    protected abstract void registerServerCommand(MValue<T> value);
}