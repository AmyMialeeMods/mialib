package xyz.amymialee.mialib.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import xyz.amymialee.mialib.Mialib;

/**
 * Stores values for telling if a player is holding attack or use.
 * Also provides the amount of time they have been held.
 */
public class HoldingComponent implements AutoSyncedComponent, CommonTickingComponent {
	public static final ComponentKey<HoldingComponent> KEY = ComponentRegistry.getOrCreate(Mialib.id("holding"), HoldingComponent.class);
	private final PlayerEntity player;
	private boolean attacking = false;
	private boolean using = false;
	private int tickAttacking = 0;
	private int tickUsing = 0;

	public HoldingComponent(PlayerEntity player) {
		this.player = player;
	}

	public void sync() {
		KEY.sync(this.player);
	}

	public boolean isAttacking() {
		return this.attacking;
	}

	public int getAttackTicks() {
		return this.tickAttacking;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
		this.sync();
	}

	public boolean isUsing() {
		return this.using;
	}

	public int getUsageTicks() {
		return this.tickUsing;
	}

	public void setUsing(boolean using) {
		this.using = using;
		this.sync();
	}

	@Override
	public void tick() {
		if (this.attacking) {
			this.tickAttacking++;
		} else {
			this.tickAttacking = 0;
		}
		if (this.using) {
			this.tickUsing++;
		} else {
			this.tickUsing = 0;
		}
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		this.attacking = tag.getBoolean("using");
		this.using = tag.getBoolean("using");
		this.tickAttacking = tag.getInt("tickAttacking");
		this.tickUsing = tag.getInt("tickUsing");
	}

	@Override
	public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putBoolean("using", this.attacking);
		tag.putBoolean("using", this.using);
		tag.putInt("tickAttacking", this.tickAttacking);
		tag.putInt("tickUsing", this.tickUsing);
	}

	@Override
	public boolean isRequiredOnClient() {
		return false;
	}
}