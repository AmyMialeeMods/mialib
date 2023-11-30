package xyz.amymialee.mialib.templates;

import net.minecraft.item.ArmorItem;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.function.Function;

public class EquipmentMaterialInstance implements EquipmentMaterial {
    public static final EquipmentMaterialInstance EMPTY = new EquipmentMaterialInstance("empty");
    private final String name;
    private Function<ArmorItem.Type, Integer> armorDurability = (type) -> 0;
    private Function<ArmorItem.Type, Integer> protection = (type) -> 0;
    private SoundEvent equipSound = SoundEvents.ITEM_ARMOR_EQUIP_LEATHER;
    private float toughness = 0;
    private float knockbackResistance = 0;
    private int toolDurability = 0;
    private float miningSpeedMultiplier = 1;
    private float attackDamage = 0;
    private int miningLevel = 0;
    private int enchantability = 0;
    private Ingredient repairIngredient = Ingredient.EMPTY;

    public EquipmentMaterialInstance(String name) {
        this.name = name;
    }

    public EquipmentMaterialInstance setArmorDurability(Function<ArmorItem.Type, Integer> armorDurability) {
        this.armorDurability = armorDurability;
        return this;
    }

    public EquipmentMaterialInstance setProtection(Function<ArmorItem.Type, Integer> protection) {
        this.protection = protection;
        return this;
    }

    public EquipmentMaterialInstance setEquipSound(SoundEvent equipSound) {
        this.equipSound = equipSound;
        return this;
    }

    public EquipmentMaterialInstance setToughness(float toughness) {
        this.toughness = toughness;
        return this;
    }

    public EquipmentMaterialInstance setKnockbackResistance(float knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
        return this;
    }

    public EquipmentMaterialInstance setToolDurability(int toolDurability) {
        this.toolDurability = toolDurability;
        return this;
    }

    public EquipmentMaterialInstance setMiningSpeedMultiplier(float miningSpeedMultiplier) {
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        return this;
    }

    public EquipmentMaterialInstance setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }

    public EquipmentMaterialInstance setMiningLevel(int miningLevel) {
        this.miningLevel = miningLevel;
        return this;
    }

    public EquipmentMaterialInstance setEnchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    public EquipmentMaterialInstance setRepairIngredient(Ingredient repairIngredient) {
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
    public int getDurability() {
        return this.toolDurability;
    }

    @Override
    public float getMiningSpeedMultiplier() {
        return this.miningSpeedMultiplier;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getMiningLevel() {
        return this.miningLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }
}