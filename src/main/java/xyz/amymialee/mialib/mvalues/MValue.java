package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MValue<T> {
    public static final MValueBoolean BOOLEAN_TRUE = new MValueBoolean(true);
    public static final MValueBoolean BOOLEAN_FALSE = new MValueBoolean(false);
    public static final MValueInteger INTEGER = new MValueInteger(100, 1, 100);
    public static final MValueFloat FLOAT = new MValueFloat(1f, 0f, 1f);
    public static final MValueLong LONG = new MValueLong(100, 1, 100);
    public static final MValueDouble DOUBLE = new MValueDouble(1d, 0d, 1d);
    public static final MValuePercent PERCENT = new MValuePercent(1d);
    public static final MValueSeconds SECONDS = new MValueSeconds(30f, 0f, 60f);
    public final Identifier id;
    public final String translationKey;
    public final MValueType<T> type;
    public final PermissionCheck permissionCheck;
    public final Predicate<Player> canChange;
    public final Consumer<MValue<T>> onChange;
    T value;

    public MValue(Identifier id, String translationKey, @NotNull MValueType<T> type, PermissionCheck permissionCheck, Predicate<Player> canChange, Consumer<MValue<T>> onChange) {
        this.id = id;
        this.translationKey = translationKey;
        this.type = type;
        this.permissionCheck = permissionCheck;
        this.canChange = canChange;
        this.onChange = onChange;
        this.value = type.defaultValue;
    }

    public static <T> @NotNull MValueBuilder<T> of(Identifier id, MValueType<T> type) {
        return new MValueBuilder<>(id, type);
    }

    public @NotNull String getTranslationKey() {
        return this.translationKey;
    }

    public String getValueAsString() {
        return this.type.getValueAsString(this);
    }

    public @NotNull Component getText() {
        return Component.translatable(this.getTranslationKey());
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.type.set(this, value);
        this.onChange.accept(this);
    }

    public CompoundTag writeNbt(CompoundTag compound) {
        return this.type.writeNbt(compound, this);
    }

    public void readNbt(CompoundTag compound) {
        this.type.readNbt(compound, this);
    }

    public JsonElement writeJson() {
        return this.type.writeJson(this);
    }

    public void readJson(JsonElement json) {
        this.type.readJson(json, this);
    }
}