package xyz.amymialee.mialib.templates;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.function.Function;

public interface EquipmentMaterial extends ToolMaterial, ArmorMaterial {
    @Override
    default int getDurability(ArmorItem.Type type) {
        return 0;
    }

    @Override
    default int getProtection(ArmorItem.Type type) {
        return 0;
    }

    @Override
    default SoundEvent getEquipSound() {
        return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    }

    @Override
    default String getName() {
        return "mialib";
    }

    @Override
    default float getToughness() {
        return 0;
    }

    @Override
    default float getKnockbackResistance() {
        return 0;
    }

    @Override
    default int getDurability() {
        return 0;
    }

    @Override
    default float getMiningSpeedMultiplier() {
        return 1;
    }

    @Override
    default float getAttackDamage() {
        return 0;
    }

    @Override
    default int getMiningLevel() {
        return 0;
    }

    @Override
    default int getEnchantability() {
        return 0;
    }

    @Override
    default Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }
}