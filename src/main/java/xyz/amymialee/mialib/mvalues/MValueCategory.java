package xyz.amymialee.mialib.mvalues;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import xyz.amymialee.mialib.util.runnables.HoldingSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MValueCategory {
    public final List<MValue<?>> values = new ArrayList<>();
    public final Identifier id;
    public final Supplier<ItemStack> stackSupplier;
    public final Identifier backgroundTexture;

    public MValueCategory(Identifier id, Supplier<ItemStack> stackSupplier, Identifier backgroundTexture) {
        this.id = id;
        this.stackSupplier = stackSupplier;
        this.backgroundTexture = backgroundTexture;
    }

    public MValueCategory(Identifier id, ItemStack stackSupplier, Identifier backgroundTexture) {
        this.id = id;
        this.stackSupplier = new HoldingSupplier<>(stackSupplier);
        this.backgroundTexture = backgroundTexture;
    }

    public String getTranslationKey() {
        return "mvaluecategory.%s.%s".formatted(this.id.getNamespace(), this.id.getPath());
    }
}