package xyz.amymialee.mialib.templates;

import net.minecraft.block.Block;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;

@SuppressWarnings("unused")
public class MToolMaterial implements ToolMaterial {
    public static final MToolMaterial EMPTY = new MToolMaterial();
    private int toolDurability = 0;
    private float miningSpeedMultiplier = 1;
    private float attackDamage = 0;
    private TagKey<Block> inverseTag = BlockTags.AIR;
    private int enchantability = 0;
    private Ingredient repairIngredient = Ingredient.EMPTY;

    public MToolMaterial() {}

    public MToolMaterial(int toolDurability, float miningSpeedMultiplier, float attackDamage, TagKey<Block> inverseTag, int enchantability, Ingredient repairIngredient) {
        this.toolDurability = toolDurability;
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        this.attackDamage = attackDamage;
        this.inverseTag = inverseTag;
        this.enchantability = enchantability;
        this.repairIngredient = repairIngredient;
    }

    public MToolMaterial copy() {
        return new MToolMaterial(this.toolDurability, this.miningSpeedMultiplier, this.attackDamage, this.inverseTag, this.enchantability, this.repairIngredient);
    }

    public MToolMaterial setToolDurability(int toolDurability) {
        this.toolDurability = toolDurability;
        return this;
    }

    public MToolMaterial setMiningSpeedMultiplier(float miningSpeedMultiplier) {
        this.miningSpeedMultiplier = miningSpeedMultiplier;
        return this;
    }

    public MToolMaterial setAttackDamage(float attackDamage) {
        this.attackDamage = attackDamage;
        return this;
    }

    public MToolMaterial setInverseTag(TagKey<Block> inverseTag) {
        this.inverseTag = inverseTag;
        return this;
    }

    public MToolMaterial setEnchantability(int enchantability) {
        this.enchantability = enchantability;
        return this;
    }

    public MToolMaterial setRepairIngredient(Ingredient repairIngredient) {
        this.repairIngredient = repairIngredient;
        return this;
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
    public TagKey<Block> getInverseTag() {
        return this.inverseTag;
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