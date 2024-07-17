package xyz.amymialee.mialib.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.amymialee.mialib.util.interfaces.MEnchantment;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements MEnchantment {
    @Unique private Identifier id;
    @Unique private TagKey<Item> extraItems;

    @Inject(method = "isSupportedItem", at = @At("RETURN"), cancellable = true)
    private void mialib$allowMoreSupported(ItemStack stack, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && this.extraItems != null && stack.isIn(this.extraItems)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isAcceptableItem", at = @At("RETURN"), cancellable = true)
    private void mialib$allowMoreAcceptable(ItemStack stack, @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && this.extraItems != null && stack.isIn(this.extraItems)) {
            cir.setReturnValue(true);
        }
    }

    @Override
    public Identifier mialib$getId() {
        return this.id;
    }

    @Override
    public void mialib$setId(@NotNull Identifier id) {
        this.id = id;
        this.extraItems = TagKey.of(Registries.ITEM.getKey(), Identifier.of(id.getNamespace(), "enchantable/" + id.getPath()));
    }

    @Override
    public TagKey<Item> mialib$getExtraItems() {
        return this.extraItems;
    }

    @Mixin(Enchantment.Builder.class)
    private static class BuilderMixin {
        @Inject(method = "build", at = @At("RETURN"))
        private void mialib$setId(Identifier id, @NotNull CallbackInfoReturnable<Enchantment> cir) {
            cir.getReturnValue().mialib$setId(id);
        }
    }
}