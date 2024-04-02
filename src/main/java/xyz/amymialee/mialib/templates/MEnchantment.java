package xyz.amymialee.mialib.templates;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import xyz.amymialee.mialib.util.runnables.QuadConsumer;
import xyz.amymialee.mialib.util.runnables.QuadFunction;
import xyz.amymialee.mialib.util.runnables.TriFunction;

import java.util.function.BiFunction;

public class MEnchantment extends Enchantment {
	private BiFunction<MEnchantment, Integer, Integer> minLevel = (enchantment, original) -> original;
	private BiFunction<MEnchantment, Integer, Integer> maxLevel = (enchantment, original) -> original;
	private TriFunction<MEnchantment, Integer, Integer, Integer> minPower = (enchantment, original, level) -> original;
	private TriFunction<MEnchantment, Integer, Integer, Integer> maxPower = (enchantment, original, level) -> original;
	private QuadFunction<MEnchantment, Integer, Integer, DamageSource, Integer> protectionAmount = (enchantment, original, level, source) -> original;
	private QuadFunction<MEnchantment, Float, Integer, EntityGroup, Float> attackDamage = (enchantment, original, level, group) -> original;
	private TriFunction<MEnchantment, Boolean, Enchantment, Boolean> canAccept = (enchantment, original, other) -> original;
	private TriFunction<MEnchantment, Boolean, ItemStack, Boolean> isAcceptableItem = (enchantment, original, stack) -> original;
	private QuadConsumer<MEnchantment, LivingEntity, Entity, Integer> onTargetDamaged = (enchantment, user, target, level) -> {};
	private QuadConsumer<MEnchantment, LivingEntity, Entity, Integer> onUserDamaged = (enchantment, user, target, level) -> {};
	private BiFunction<MEnchantment, Boolean, Boolean> isTreasure = (enchantment, original) -> original;
	private BiFunction<MEnchantment, Boolean, Boolean> isCursed = (enchantment, original) -> original;
	private BiFunction<MEnchantment, Boolean, Boolean> isAvailableForEnchantedBookOffer = (enchantment, original) -> original;
	private BiFunction<MEnchantment, Boolean, Boolean> isAvailableForRandomSelection = (enchantment, original) -> original;

	public MEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot ... slotTypes) {
		super(weight, type, slotTypes);
	}

	public MEnchantment setMinLevel(BiFunction<MEnchantment, Integer, Integer> minLevel) {
		this.minLevel = minLevel;
		return this;
	}

	public MEnchantment setMaxLevel(BiFunction<MEnchantment, Integer, Integer> maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}

	public MEnchantment setMinPower(TriFunction<MEnchantment, Integer, Integer, Integer> minPower) {
		this.minPower = minPower;
		return this;
	}

	public MEnchantment setMaxPower(TriFunction<MEnchantment, Integer, Integer, Integer> maxPower) {
		this.maxPower = maxPower;
		return this;
	}

	public MEnchantment setProtectionAmount(QuadFunction<MEnchantment, Integer, Integer, DamageSource, Integer> protectionAmount) {
		this.protectionAmount = protectionAmount;
		return this;
	}

	public MEnchantment setAttackDamage(QuadFunction<MEnchantment, Float, Integer, EntityGroup, Float> attackDamage) {
		this.attackDamage = attackDamage;
		return this;
	}

	public MEnchantment setCanAccept(TriFunction<MEnchantment, Boolean, Enchantment, Boolean> canAccept) {
		this.canAccept = canAccept;
		return this;
	}

	public MEnchantment setIsAcceptableItem(TriFunction<MEnchantment, Boolean, ItemStack, Boolean> isAcceptableItem) {
		this.isAcceptableItem = isAcceptableItem;
		return this;
	}

	public MEnchantment setOnTargetDamaged(QuadConsumer<MEnchantment, LivingEntity, Entity, Integer> onTargetDamaged) {
		this.onTargetDamaged = onTargetDamaged;
		return this;
	}

	public MEnchantment setOnUserDamaged(QuadConsumer<MEnchantment, LivingEntity, Entity, Integer> onUserDamaged) {
		this.onUserDamaged = onUserDamaged;
		return this;
	}

	public MEnchantment setIsTreasure(BiFunction<MEnchantment, Boolean, Boolean> isTreasure) {
		this.isTreasure = isTreasure;
		return this;
	}

	public MEnchantment setIsCursed(BiFunction<MEnchantment, Boolean, Boolean> isCursed) {
		this.isCursed = isCursed;
		return this;
	}

	public MEnchantment setIsAvailableForEnchantedBookOffer(BiFunction<MEnchantment, Boolean, Boolean> isAvailableForEnchantedBookOffer) {
		this.isAvailableForEnchantedBookOffer = isAvailableForEnchantedBookOffer;
		return this;
	}

	public MEnchantment setIsAvailableForRandomSelection(BiFunction<MEnchantment, Boolean, Boolean> isAvailableForRandomSelection) {
		this.isAvailableForRandomSelection = isAvailableForRandomSelection;
		return this;
	}

	@Override
	public int getMinLevel() {
		return this.minLevel.apply(this, super.getMinLevel());
	}

	@Override
	public int getMaxLevel() {
		return this.maxLevel.apply(this, super.getMaxLevel());
	}

	@Override
	public int getMinPower(int level) {
		return this.minPower.apply(this, super.getMinPower(level), level);
	}

	@Override
	public int getMaxPower(int level) {
		return this.maxPower.apply(this, super.getMaxPower(level), level);
	}

	@Override
	public int getProtectionAmount(int level, DamageSource source) {
		return this.protectionAmount.apply(this, super.getProtectionAmount(level, source), level, source);
	}

	@Override
	public float getAttackDamage(int level, EntityGroup group) {
		return this.attackDamage.apply(this, super.getAttackDamage(level, group), level, group);
	}

	@Override
	protected boolean canAccept(Enchantment other) {
		return this.canAccept.apply(this, super.canAccept(other), other);
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return this.isAcceptableItem.apply(this, super.isAcceptableItem(stack), stack);
	}

	@Override
	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
		this.onTargetDamaged.accept(this, user, target, level);
	}

	@Override
	public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
		this.onUserDamaged.accept(this, user, attacker, level);
	}

	@Override
	public boolean isTreasure() {
		return this.isTreasure.apply(this, super.isTreasure());
	}

	@Override
	public boolean isCursed() {
		return this.isCursed.apply(this, super.isCursed());
	}

	@Override
	public boolean isAvailableForEnchantedBookOffer() {
		return this.isAvailableForEnchantedBookOffer.apply(this, super.isAvailableForEnchantedBookOffer());
	}

	@Override
	public boolean isAvailableForRandomSelection() {
		return this.isAvailableForRandomSelection.apply(this, super.isAvailableForRandomSelection());
	}
}