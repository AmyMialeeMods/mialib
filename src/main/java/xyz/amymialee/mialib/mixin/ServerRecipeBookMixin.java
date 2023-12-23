package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.MiaLib;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    @ModifyExpressionValue(method = "unlockRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/Recipe;isIgnoredInRecipeBook()Z"))
    private boolean mialib$ignoreRecipeInUncraftableTag(boolean original, Collection<RecipeEntry<?>> recipes, ServerPlayerEntity player, @Local RecipeEntry<?> recipeEntry) {
        if (!original) {
            var registries = player.getWorld().getRegistryManager();
            var output = recipeEntry.value().getResult(registries);
            return output.isIn(MiaLib.UNCRAFTABLE);
        }

        return false;
    }

    @ModifyExpressionValue(method = "sendInitRecipesPacket", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerRecipeBook;recipes:Ljava/util/Set;"))
    private Set<Identifier> mialib$ignoreRecipesInUncraftableTagInitPacket(Set<Identifier> original, ServerPlayerEntity player) {
        var registries = player.getWorld().getRegistryManager();
        var recipes = player.getWorld().getRecipeManager();
        var out = new HashSet<>(original);
        out.removeIf(i -> recipes.get(i).filter(r -> !(r.value().getResult(registries).isIn(MiaLib.UNCRAFTABLE))).isEmpty());
        return out;
    }

    @ModifyExpressionValue(method = "sendInitRecipesPacket", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerRecipeBook;toBeDisplayed:Ljava/util/Set;"))
    private Set<Identifier> mialib$ignoreDisplayInUncraftableTagInitPacket(Set<Identifier> original, ServerPlayerEntity player) {
        var registries = player.getWorld().getRegistryManager();
        var recipes = player.getWorld().getRecipeManager();
        var out = new HashSet<>(original);
        out.removeIf(i -> recipes.get(i).filter(r -> !(r.value().getResult(registries).isIn(MiaLib.UNCRAFTABLE))).isEmpty());
        return out;
    }
}
