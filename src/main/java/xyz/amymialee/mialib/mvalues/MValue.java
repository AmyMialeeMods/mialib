package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.mixin.interfaces.PlayerEntityMixin;

import java.util.function.Function;
import java.util.function.Predicate;

public final class MValue<T> {
    public static final MValueCategory DEFAULT_CATEGORY = new MValueCategory(Mialib.id(Mialib.MOD_ID), Items.DIAMOND, Identifier.ofVanilla("textures/block/purple_concrete.png"), 16, 16);
    public static final MValueType.MValueBoolean BOOLEAN_TRUE = new MValueType.MValueBoolean(true);
    public static final MValueType.MValueBoolean BOOLEAN_FALSE = new MValueType.MValueBoolean(false);
    public static final MValueType.MValueInteger INTEGER = new MValueType.MValueInteger(100, 1, 100);
    public static final MValueType.MValueFloat FLOAT = new MValueType.MValueFloat(1f, 0f, 1f);
    public static final MValueType.MValueLong LONG = new MValueType.MValueLong(100, 1, 100);
    public static final MValueType.MValueDouble DOUBLE = new MValueType.MValueDouble(1d, 0d, 1d);
    public final Identifier id;
    public final MValueType<T> type;
    public final Function<MValue<T>, ItemStack> stackFunction;
    public final int permissionLevel;
    public final boolean clientSide;
    public final Predicate<PlayerEntity> canChange;
    T value;

    public MValue(Identifier id, @NotNull MValueType<T> type, Function<MValue<T>, ItemStack> stackFunction, int permissionLevel, boolean clientSide, Predicate<PlayerEntity> canChange) {
        this.id = id;
        this.type = type;
        this.stackFunction = stackFunction;
        this.permissionLevel = permissionLevel;
        this.clientSide = clientSide;
        this.canChange = canChange;
        this.value = type.defaultValue;
    }

    public static <T> @NotNull MValueBuilder<T> of(Identifier id, MValueType<T> type) {
        return new MValueBuilder<>(id, type);
    }

    public @NotNull String getTranslationKey() {
        return "mvalue.%s.%s".formatted(this.id.getNamespace(), this.id.getPath());
    }

    public @NotNull String getDescriptionTranslationKey() {
        return "mvalue.%s.%s.desc".formatted(this.id.getNamespace(), this.id.getPath());
    }

    public @NotNull Text getText() {
        return Text.translatable(this.getTranslationKey());
    }

    public @NotNull Text getDescription() {
        return Text.translatable(this.getDescriptionTranslationKey());
    }

    public ItemStack getStack() {
        return this.stackFunction.apply(this);
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        if (this.type.set(this, value)) MValueManager.INSTANCE.onChange(this);
    }

    public NbtCompound writeNbt(NbtCompound compound) {
        return this.type.writeNbt(compound, this);
    }

    public void readNbt(NbtCompound compound) {
        this.type.readNbt(compound, this);
    }

    public JsonElement writeJson() {
        return this.type.writeJson(this);
    }

    public void readJson(JsonElement json) {
        this.type.readJson(json, this);
    }

    public ClickableWidget getWidget(int x, int y) {
        return this.type.getWidget(x, y, this);
    }
}