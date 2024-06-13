package xyz.amymialee.mialib.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.inventory.Inventory;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.modules.ItemModule;

import java.util.List;
import java.util.Optional;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
    @Inject(method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;", at = @At(value = "RETURN"), cancellable = true)
    private <C extends Inventory, T extends Recipe<C>> void mialib$killRecipe(RecipeType<T> type, C inventory, World world, @NotNull CallbackInfoReturnable<Optional<T>> cir) {
        var optional = cir.getReturnValue();
        if (optional.isPresent() && optional.get().getOutput(world.getRegistryManager()).isIn(ItemModule.UNCRAFTABLE)) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;Lnet/minecraft/util/Identifier;)Ljava/util/Optional;", at = @At(value = "RETURN"), cancellable = true)
    private <C extends Inventory, T extends Recipe<C>> void mialib$killRecipe(RecipeType<T> type, C inventory, World world, @Nullable Identifier id, @NotNull CallbackInfoReturnable<Optional<Pair<Identifier, T>>> cir) {
        var optional = cir.getReturnValue();
        if (optional.isPresent() && optional.get().getSecond().getOutput(world.getRegistryManager()).isIn(ItemModule.UNCRAFTABLE)) {
            cir.setReturnValue(Optional.empty());
        }
    }

    @Inject(method = "getAllMatches", at = @At(value = "RETURN"), cancellable = true)
    private <C extends Inventory, T extends Recipe<C>> void mialib$killRecipes(RecipeType<T> type, C inventory, World world, @NotNull CallbackInfoReturnable<List<T>> cir) {
        var list = cir.getReturnValue();
        for (var i = 0; i < list.size(); i++) {
            if (list.get(i).getOutput(world.getRegistryManager()).isIn(ItemModule.UNCRAFTABLE)) {
                list.remove(i);
                i--;
            }
        }
        cir.setReturnValue(list);
    }
}