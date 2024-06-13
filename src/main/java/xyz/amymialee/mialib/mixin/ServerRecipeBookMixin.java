package xyz.amymialee.mialib.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerRecipeBook;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xyz.amymialee.mialib.modules.ItemModule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Mixin(ServerRecipeBook.class)
public class ServerRecipeBookMixin {
    @ModifyExpressionValue(method = "unlockRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/recipe/Recipe;isIgnoredInRecipeBook()Z"))
    private boolean mialib$ignoreRecipeInUncraftableTag(boolean original, Collection<Recipe<?>> recipes, ServerPlayerEntity player, @Local(ordinal = 0) Recipe<?> recipe) {
        if (!original) {
            var registries = player.getWorld().getRegistryManager();
            var output = recipe.getOutput(registries);
            return output.isIn(ItemModule.UNCRAFTABLE);
        }
        return false;
    }

    @ModifyExpressionValue(method = "sendInitRecipesPacket", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerRecipeBook;recipes:Ljava/util/Set;"))
    private @NotNull Set<Identifier> mialib$ignoreRecipesInUncraftableTagInitPacket(Set<Identifier> original, @NotNull ServerPlayerEntity player) {
        var registries = player.getWorld().getRegistryManager();
        var recipes = player.getWorld().getRecipeManager();
        var out = new HashSet<>(original);
        out.removeIf(i -> recipes.get(i).filter(r -> !(r.getOutput(registries).isIn(ItemModule.UNCRAFTABLE))).isEmpty());
        return out;
    }

    @ModifyExpressionValue(method = "sendInitRecipesPacket", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerRecipeBook;toBeDisplayed:Ljava/util/Set;"))
    private @NotNull Set<Identifier> mialib$ignoreDisplayInUncraftableTagInitPacket(Set<Identifier> original, @NotNull ServerPlayerEntity player) {
        var registries = player.getWorld().getRegistryManager();
        var recipes = player.getWorld().getRecipeManager();
        var out = new HashSet<>(original);
        out.removeIf(i -> recipes.get(i).filter(r -> !(r.getOutput(registries).isIn(ItemModule.UNCRAFTABLE))).isEmpty());
        return out;
    }
}