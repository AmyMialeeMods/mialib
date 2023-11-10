package xyz.amymialee.mialib.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class MEnchantment extends Enchantment {
	private Supplier<Integer> minLevel = super::getMinLevel;
	private Supplier<Integer> maxLevel = super::getMaxLevel;
	private Function<Integer, Integer> minPower = super::getMinPower;
	private Function<Integer, Integer> maxPower = super::getMaxPower;
	private BiFunction<Integer, DamageSource, Integer> protectionAmount = super::getProtectionAmount;
	private BiFunction<Integer, EntityGroup, Float> attackDamage = super::getAttackDamage;
	private Function<Enchantment, Boolean> canAccept = super::canAccept;
	private Function<ItemStack, Boolean> isAcceptableItem = super::isAcceptableItem;
	private TriConsumer<LivingEntity, Entity, Integer> onTargetDamaged = super::onTargetDamaged;
	private TriConsumer<LivingEntity, Entity, Integer> onUserDamaged = super::onUserDamaged;
	private Supplier<Boolean> isTreasure = super::isTreasure;
	private Supplier<Boolean> isCursed = super::isCursed;
	private Supplier<Boolean> isAvailableForEnchantedBookOffer = super::isAvailableForEnchantedBookOffer;
	private Supplier<Boolean> isAvailableForRandomSelection = super::isAvailableForRandomSelection;

	protected MEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot ... slotTypes) {
		super(weight, type, slotTypes);
	}

	public void setMinLevel(Supplier<Integer> minLevel) {
		this.minLevel = minLevel;
	}

	public void setMaxLevel(Supplier<Integer> maxLevel) {
		this.maxLevel = maxLevel;
	}

	public void setMinPower(Function<Integer, Integer> minPower) {
		this.minPower = minPower;
	}

	public void setMaxPower(Function<Integer, Integer> maxPower) {
		this.maxPower = maxPower;
	}

	public void setProtectionAmount(BiFunction<Integer, DamageSource, Integer> protectionAmount) {
		this.protectionAmount = protectionAmount;
	}

	public void setAttackDamage(BiFunction<Integer, EntityGroup, Float> attackDamage) {
		this.attackDamage = attackDamage;
	}

	public void setCanAccept(Function<Enchantment, Boolean> canAccept) {
		this.canAccept = canAccept;
	}

	public void setIsAcceptableItem(Function<ItemStack, Boolean> isAcceptableItem) {
		this.isAcceptableItem = isAcceptableItem;
	}

	public void setOnTargetDamaged(TriConsumer<LivingEntity, Entity, Integer> onTargetDamaged) {
		this.onTargetDamaged = onTargetDamaged;
	}

	public void setOnUserDamaged(TriConsumer<LivingEntity, Entity, Integer> onUserDamaged) {
		this.onUserDamaged = onUserDamaged;
	}

	public void setIsTreasure(Supplier<Boolean> isTreasure) {
		this.isTreasure = isTreasure;
	}

	public void setIsCursed(Supplier<Boolean> isCursed) {
		this.isCursed = isCursed;
	}

	public void setIsAvailableForEnchantedBookOffer(Supplier<Boolean> isAvailableForEnchantedBookOffer) {
		this.isAvailableForEnchantedBookOffer = isAvailableForEnchantedBookOffer;
	}

	public void setIsAvailableForRandomSelection(Supplier<Boolean> isAvailableForRandomSelection) {
		this.isAvailableForRandomSelection = isAvailableForRandomSelection;
	}

	@Override
	public int getMinLevel() {
		return this.minLevel.get();
	}

	@Override
	public int getMaxLevel() {
		return this.maxLevel.get();
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
		return this.isTreasure.get();
	}

	@Override
	public boolean isCursed() {
		return this.isCursed.get();
	}

	@Override
	public boolean isAvailableForEnchantedBookOffer() {
		return this.isAvailableForEnchantedBookOffer.get();
	}

	@Override
	public boolean isAvailableForRandomSelection() {
		return this.isAvailableForRandomSelection.get();
	}
}
