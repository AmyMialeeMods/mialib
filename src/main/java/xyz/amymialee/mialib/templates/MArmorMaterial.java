package xyz.amymialee.mialib.templates;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class MArmorMaterial implements ArmorMaterial {
    public static final MArmorMaterial EMPTY = new MArmorMaterial("mialib");
    private final String name;
    private Function<ArmorItem.Type, Integer> armorDurability = (type) -> 0;
    private Function<ArmorItem.Type, Integer> protection = (type) -> 0;
    private SoundEvent equipSound = SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    private float toughness = 0;
    private float knockbackResistance = 0;
    private int enchantability = 0;
    private Ingredient repairIngredient = Ingredient.EMPTY;

    public MArmorMaterial(String name) {
        this.name = name;
    }

    public MArmorMaterial(String name, Function<ArmorItem.Type, Integer> armorDurability, Function<ArmorItem.Type, Integer> protection, SoundEvent equipSound, float toughness, float knockbackResistance, int enchantability, Ingredient repairIngredient) {
        this.name = name;
        this.armorDurability = armorDurability;
        this.protection = protection;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    public MArmorMaterial copy(String name) {
        return new MArmorMaterial(name, this.armorDurability, this.protection, this.equipSound, this.toughness, this.knockbackResistance, this.enchantability, this.repairIngredient);
    }

    public MArmorMaterial setArmorDurability(Function<ArmorItem.Type, Integer> armorDurability) {
        this.armorDurability = armorDurability;
        return this;
    }

    public MArmorMaterial setArmorDurability(int[] armorDurability) {
        this.armorDurability = new ArmorTypeFunction(armorDurability);
        return this;
    }

    public MArmorMaterial setProtection(Function<ArmorItem.Type, Integer> protection) {
        this.protection = protection;
        return this;
    }

    public MArmorMaterial setProtection(int[] protection) {
        this.protection = new ArmorTypeFunction(protection);
        return this;
    }

    public MArmorMaterial setEquipSound(SoundEvent equipSound) {
        this.equipSound = equipSound;
        return this;
    }

    public MArmorMaterial setToughness(float toughness) {
        this.toughness = toughness;
        return this;
    }

    public MArmorMaterial setKnockbackResistance(float knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    public MArmorMaterial setEnchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    public MArmorMaterial setRepairIngredient(Ingredient repairIngredient) {
        this.repairIngredient = repairIngredient;
        return this;
    }

    @Override
    public int getDurability(ArmorItem.Type type) {
        return this.armorDurability.apply(type);
    }

    @Override
    public int getProtection(ArmorItem.Type type) {
        return this.protection.apply(type);
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }

    private record ArmorTypeFunction(int[] values) implements Function<ArmorItem.Type, Integer> {
        @Override
        public Integer apply(ArmorItem.@NotNull Type type) {
            return this.values[type.ordinal()];
        }
    }
}