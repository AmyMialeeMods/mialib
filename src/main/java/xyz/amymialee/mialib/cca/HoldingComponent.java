package xyz.amymialee.mialib.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
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

	public boolean isUsing() {
		return this.using;
	}

	public int getAttackTicks() {
		return this.tickAttacking;
	}

	public int getUsageTicks() {
		return this.tickUsing;
	}

	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
		this.sync();
	}

	public void setUsing(boolean using) {
		this.using = using;
		this.sync();
	}

	public @Override void tick() {
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

	public @Override boolean isRequiredOnClient() {
		return false;
	}

	public @Override void readData(@NotNull ReadView readView) {
		this.attacking = readView.getBoolean("using", false);
		this.using = readView.getBoolean("using", false);
		this.tickAttacking = readView.getInt("tickAttacking", 0);
		this.tickUsing = readView.getInt("tickUsing", 0);
	}

	public @Override void writeData(@NotNull WriteView writeView) {
		writeView.putBoolean("using", this.attacking);
		writeView.putBoolean("using", this.using);
		writeView.putInt("tickAttacking", this.tickAttacking);
		writeView.putInt("tickUsing", this.tickUsing);
	}
}