package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;

public abstract class MValueType<T> {
    protected T defaultValue;

    public abstract boolean set(MValue<T> mValue, T value);

    public String getValueAsString(@NotNull MValue<T> value) {
        return String.valueOf(value.get());
    }

    public abstract NbtCompound writeNbt(NbtCompound compound, MValue<T> value);

    public abstract void readNbt(NbtCompound compound, MValue<T> value);

    public abstract JsonElement writeJson(MValue<T> value);

    public abstract void readJson(JsonElement json, MValue<T> value);

    public void registerCommand(@NotNull MValue<T> value) {
        if (value.clientSide) {
            this.registerClientCommand(value);
        } else {
            this.registerServerCommand(value);
        }
    }

    protected abstract void registerServerCommand(MValue<T> value);

    protected abstract void registerClientCommand(MValue<T> value);

    @Environment(EnvType.CLIENT)
    public abstract Object getWidget(int x, int y, MValue<T> mValue);
}