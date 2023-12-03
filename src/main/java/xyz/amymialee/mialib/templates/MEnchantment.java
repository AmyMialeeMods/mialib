package xyz.amymialee.mialib.templates;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import xyz.amymialee.mialib.util.TriConsumer;

import java.util.function.BiFunction;
import java.util.function.Function;

public class MEnchantment extends Enchantment {
	private int minLevel = super.getMinLevel();
	private int maxLevel = super.getMaxLevel();
	private Function<Integer, Integer> minPower = super::getMinPower;
	private Function<Integer, Integer> maxPower = super::getMaxPower;
	private BiFunction<Integer, DamageSource, Integer> protectionAmount = super::getProtectionAmount;
	private BiFunction<Integer, EntityGroup, Float> attackDamage = super::getAttackDamage;
	private Function<Enchantment, Boolean> canAccept = super::canAccept;
	private Function<ItemStack, Boolean> isAcceptableItem = super::isAcceptableItem;
	private TriConsumer<LivingEntity, Entity, Integer> onTargetDamaged = super::onTargetDamaged;
	private TriConsumer<LivingEntity, Entity, Integer> onUserDamaged = super::onUserDamaged;
	private boolean isTreasure = super.isTreasure();
	private boolean isCursed = super.isCursed();
	private boolean isAvailableForEnchantedBookOffer = super.isAvailableForEnchantedBookOffer();
	private boolean isAvailableForRandomSelection = super.isAvailableForRandomSelection();

	public MEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot ... slotTypes) {
		super(weight, type, slotTypes);
	}

	public MEnchantment setMinLevel(int minLevel) {
		this.minLevel = minLevel;
		return this;
	}

	public MEnchantment setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
		return this;
	}

	public MEnchantment setMinPower(Function<Integer, Integer> minPower) {
		this.minPower = minPower;
		return this;
	}

	public MEnchantment setMaxPower(Function<Integer, Integer> maxPower) {
		this.maxPower = maxPower;
		return this;
	}

	public MEnchantment setProtectionAmount(BiFunction<Integer, DamageSource, Integer> protectionAmount) {
		this.protectionAmount = protectionAmount;
		return this;
	}

	public MEnchantment setAttackDamage(BiFunction<Integer, EntityGroup, Float> attackDamage) {
		this.attackDamage = attackDamage;
		return this;
	}

	public MEnchantment setCanAccept(Function<Enchantment, Boolean> canAccept) {
		this.canAccept = canAccept;
		return this;
	}

	public MEnchantment setIsAcceptableItem(Function<ItemStack, Boolean> isAcceptableItem) {
		this.isAcceptableItem = isAcceptableItem;
		return this;
	}

	public MEnchantment setOnTargetDamaged(TriConsumer<LivingEntity, Entity, Integer> onTargetDamaged) {
		this.onTargetDamaged = onTargetDamaged;
		return this;
	}

	public MEnchantment setOnUserDamaged(TriConsumer<LivingEntity, Entity, Integer> onUserDamaged) {
		this.onUserDamaged = onUserDamaged;
		return this;
	}

	public MEnchantment setIsTreasure(boolean isTreasure) {
		this.isTreasure = isTreasure;
		return this;
	}

	public MEnchantment setIsCursed(boolean isCursed) {
		this.isCursed = isCursed;
		return this;
	}

	public MEnchantment setIsAvailableForEnchantedBookOffer(boolean isAvailableForEnchantedBookOffer) {
		this.isAvailableForEnchantedBookOffer = isAvailableForEnchantedBookOffer;
		return this;
	}

	public MEnchantment setIsAvailableForRandomSelection(boolean isAvailableForRandomSelection) {
		this.isAvailableForRandomSelection = isAvailableForRandomSelection;
		return this;
	}

	@Override
	public int getMinLevel() {
		return this.minLevel;
	}

	@Override
	public int getMaxLevel() {
		return this.maxLevel;
	}

	@Override
	public int getMinPower(int level) {
		return this.minPower.apply(level);
	}

	@Override
	public int getMaxPower(int level) {
		return this.maxPower.apply(level);
	}

	@Override
	public int getProtectionAmount(int level, DamageSource source) {
		return this.protectionAmount.apply(level, source);
	}

	@Override
	public float getAttackDamage(int level, EntityGroup group) {
		return this.attackDamage.apply(level, group);
	}

	@Override
	protected boolean canAccept(Enchantment other) {
		return this.canAccept.apply(other);
	}

	@Override
	public boolean isAcceptableItem(ItemStack stack) {
		return this.isAcceptableItem.apply(stack);
	}

	@Override
	public void onTargetDamaged(LivingEntity user, Entity target, int level) {
		this.onTargetDamaged.accept(user, target, level);
	}

	@Override
	public void onUserDamaged(LivingEntity user, Entity attacker, int level) {
		this.onUserDamaged.accept(user, attacker, level);
	}

	@Override
	public boolean isTreasure() {
		return this.isTreasure;
	}

	@Override
	public boolean isCursed() {
		return this.isCursed;
	}

	@Override
	public boolean isAvailableForEnchantedBookOffer() {
		return this.isAvailableForEnchantedBookOffer;
	}

	@Override
	public boolean isAvailableForRandomSelection() {
		return this.isAvailableForRandomSelection;
	}
}