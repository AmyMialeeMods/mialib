package xyz.amymialee.mialib.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.UUID;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
    @Shadow @Final private static EnumMap<ArmorItem.Type, UUID> MODIFIERS;

    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/ImmutableMultimap$Builder;build()Lcom/google/common/collect/ImmutableMultimap;", shift = At.Shift.BEFORE))
    private void mialib$allowKnockbackResistance(ArmorMaterial material, ArmorItem.Type type, Item.Settings settings, CallbackInfo ci, @Local(ordinal = 0) ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder) {
        if (material != ArmorMaterials.NETHERITE && material.getKnockbackResistance() > 0) {
            var uUID = MODIFIERS.get(type);
            builder.put(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, new EntityAttributeModifier(uUID, "Armor knockback resistance", material.getKnockbackResistance(), EntityAttributeModifier.Operation.ADDITION));
        }
    }
}