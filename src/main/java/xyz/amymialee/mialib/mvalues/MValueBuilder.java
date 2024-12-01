package xyz.amymialee.mialib.mvalues;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.Mialib;
import xyz.amymialee.mialib.util.runnables.CachedFunction;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MValueBuilder<T> {
    private final Identifier id;
    private final MValueType<T> type;
    private MValueCategory category = MValue.DEFAULT_CATEGORY;
    private Function<MValue<T>, ItemStack> stackFunction = new CachedFunction<>(Items.DIAMOND.getDefaultStack());
    private int permissionLevel = 4;
    private boolean clientSide = false;
    private Predicate<PlayerEntity> canChange = (p) -> true;

    protected MValueBuilder(Identifier id, MValueType<T> type) {
        this.id = id;
        this.type = type;
    }

    public MValueBuilder<T> category(MValueCategory category) {
        this.category = category;
        return this;
    }

    public MValueBuilder<T> item(@NotNull Item item) {
        return this.stack(item.getDefaultStack());
    }

    public MValueBuilder<T> item(@NotNull Supplier<Item> item) {
        return this.stack(() -> item.get().getDefaultStack());
    }

    public MValueBuilder<T> item(Function<MValue<T>, Item> item) {
        return this.stack((v) -> item.apply(v).getDefaultStack());
    }

    public MValueBuilder<T> stack(ItemStack stack) {
        return this.stack(() -> stack);
    }

    public MValueBuilder<T> stack(@NotNull Supplier<ItemStack> stack) {
        return this.stack((v) -> stack.get());
    }

    public MValueBuilder<T> stack(Function<MValue<T>, ItemStack> stack) {
        this.stackFunction = stack;
        return this;
    }

    public MValueBuilder<T> permissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
        return this;
    }

    public MValueBuilder<T> clientSide(boolean clientSide) {
        this.clientSide = clientSide;
        this.permissionLevel = 0;
        return this;
    }

    public MValueBuilder<T> predicate(Predicate<PlayerEntity> predicate) {
        this.canChange = predicate;
        return this;
    }

    public MValue<T> build() {
        var value = new MValue<>(this.id, this.type, this.stackFunction, this.permissionLevel, this.clientSide, this.canChange);
        MValueManager.register(this.category, value);
        return value;
    }
}