package xyz.amymialee.mialib.mixin;

import net.minecraft.recipe.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeManager.class)
public class RecipeManagerMixin {
//    @Inject(method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;)Ljava/util/Optional;", at = @At(value = "RETURN"), cancellable = true)
//    private <C extends Inventory, T extends Recipe<C>> void mialib$killRecipe(RecipeType<T> type, C inventory, World world, @NotNull CallbackInfoReturnable<Optional<RecipeEntry<T>>> cir) {
//        var optional = cir.getReturnValue();
//        if (optional.isPresent() && optional.get().value().getResult(world.getRegistryManager()).isIn(MiaLib.UNCRAFTABLE)) {
//            cir.setReturnValue(Optional.empty());
//        }
//    }
//
//    @Inject(method = "getFirstMatch(Lnet/minecraft/recipe/RecipeType;Lnet/minecraft/inventory/Inventory;Lnet/minecraft/world/World;Lnet/minecraft/util/Identifier;)Ljava/util/Optional;", at = @At(value = "RETURN"), cancellable = true)
//    private <C extends Inventory, T extends Recipe<C>> void mialib$killRecipe(RecipeType<T> type, C inventory, World world, @Nullable Identifier id, @NotNull CallbackInfoReturnable<Optional<Pair<Identifier, RecipeEntry<T>>>> cir) {
//        var optional = cir.getReturnValue();
//        if (optional.isPresent() && optional.get().getSecond().value().getResult(world.getRegistryManager()).isIn(MiaLib.UNCRAFTABLE)) {
//            cir.setReturnValue(Optional.empty());
//        }
//    }
//
//    @Inject(method = "getAllMatches", at = @At(value = "RETURN"), cancellable = true)
//    private <C extends Inventory, T extends Recipe<C>> void mialib$killRecipes(RecipeType<T> type, C inventory, World world, @NotNull CallbackInfoReturnable<List<RecipeEntry<T>>> cir) {
//        var list = cir.getReturnValue();
//        for (var i = 0; i < list.size(); i++) {
//            if (list.get(i).value().getResult(world.getRegistryManager()).isIn(MiaLib.UNCRAFTABLE)) {
//                list.remove(i);
//                i--;
//            }
//        }
//        cir.setReturnValue(list);
//    }
}