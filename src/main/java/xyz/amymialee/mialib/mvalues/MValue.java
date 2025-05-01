package xyz.amymialee.mialib.mvalues;

import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MValue<T> {
    public static final MValueCategory DEFAULT_CATEGORY = new MValueCategory(Mialib.id(Mialib.MOD_ID), Items.DIAMOND, Identifier.ofVanilla("textures/block/purple_concrete.png"), 16, 16);
    public static final MValueCategory INVISIBLE_CATEGORY = new MValueCategory(Mialib.id(Mialib.MOD_ID), Items.LIGHT_GRAY_STAINED_GLASS_PANE, Identifier.ofVanilla("textures/block/light_gray_stained_glass.png"), 16, 16);
    public static final MValueCategory MINECRAFT_CATEGORY = new MValueCategory(Identifier.ofVanilla("minecraft"), Items.GRASS_BLOCK, Identifier.ofVanilla("textures/block/stone.png"), 16, 16);
    public static final MValueBoolean BOOLEAN_TRUE = new MValueBoolean(true);
    public static final MValueBoolean BOOLEAN_FALSE = new MValueBoolean(false);
    public static final MValueInteger INTEGER = new MValueInteger(100, 1, 100);
    public static final MValueFloat FLOAT = new MValueFloat(1f, 0f, 1f);
    public static final MValueLong LONG = new MValueLong(100, 1, 100);
    public static final MValueDouble DOUBLE = new MValueDouble(1d, 0d, 1d);
    public static final MValuePercent PERCENT = new MValuePercent(1d, 0d, 1d);
    public static final MValueSeconds SECONDS = new MValueSeconds(30f, 0f, 60f);
    public final Identifier id;
    public final String translationKey;
    public final MValueType<T> type;
    public final Function<MValue<T>, ItemStack> stackFunction;
    public final int permissionLevel;
    public final boolean clientSide;
    public final Predicate<PlayerEntity> canChange;
    public final Consumer<MValue<T>> onChange;
    T value;

    public MValue(Identifier id, String translationKey, @NotNull MValueType<T> type, Function<MValue<T>, ItemStack> stackFunction, int permissionLevel, boolean clientSide, Predicate<PlayerEntity> canChange, Consumer<MValue<T>> onChange) {
        this.id = id;
        this.translationKey = translationKey;
        this.type = type;
        this.stackFunction = stackFunction;
        this.permissionLevel = permissionLevel;
        this.clientSide = clientSide;
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

    public @NotNull String getDescriptionTranslationKey() {
        return this.translationKey + ".desc";
    }

    public String getValueAsString() {
        return this.type.getValueAsString(this);
    }

    public @NotNull Text getText() {
        return Text.translatable(this.getTranslationKey());
    }

    public @NotNull Text getDescription() {
        var description = Text.translatable(this.getDescriptionTranslationKey());
        if (this.clientSide) description = description.append(Text.literal("\n").append(Text.translatable("%s.mvalue.clientside".formatted(Mialib.MOD_ID)).withColor(0xBD6898)));
        return description;
    }

    public ItemStack getStack() {
        return this.stackFunction.apply(this);
    }

    public T get() {
        return this.value;
    }

    public void set(T value) {
        this.type.set(this, value);
        if (this.clientSide) MVClientManager.INSTANCE.onChange(this);
        this.onChange.accept(this);
    }

    @Environment(EnvType.CLIENT)
    public void send(T value) {
        this.type.set(this, value);
        if (this.clientSide) {
            MVClientManager.INSTANCE.onChange(this);
            this.onChange.accept(this);
        } else {
            ClientPlayNetworking.send(new MValuePayload(this.id, this.type.writeNbt(new NbtCompound(), this)));
        }
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

    @Environment(EnvType.CLIENT)
    public Object getWidget(int x, int y) {
        return this.type.getWidget(x, y, this);
    }
}