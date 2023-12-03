package xyz.amymialee.mialib.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;

public class HoldingComponent implements AutoSyncedComponent, CommonTickingComponent {
	private final PlayerEntity player;
	private boolean holding = false;
	private int tick = 0;

	public HoldingComponent(PlayerEntity player) {
		this.player = player;
	}

	public void sync() {
		MiaLib.HOLDING.sync(this.player);
	}

	public boolean isHolding() {
		return this.holding;
	}

	public void setHolding(boolean holding) {
		this.holding = holding;
		this.sync();
	}

	public int getTick() {
		return this.tick;
	}

	@Override
	public void tick() {
		if (this.holding) {
			this.tick++;
		} else {
			this.tick = 0;
		}
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound tag) {
		this.holding = tag.getBoolean("holding");
	}

	@Override
	public void writeToNbt(@NotNull NbtCompound tag) {
		tag.putBoolean("holding", this.holding);
	}
}
