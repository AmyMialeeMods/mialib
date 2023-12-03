package xyz.amymialee.mialib.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import xyz.amymialee.mialib.MiaLib;

import java.util.HashMap;
import java.util.Map;

public class IdCooldownComponent implements AutoSyncedComponent, CommonTickingComponent {
	private final PlayerEntity player;
	private final Map<Identifier, Entry> cooldowns = new HashMap<>();
	private int tick;

	public IdCooldownComponent(PlayerEntity player) {
		this.player = player;
	}

	public static @NotNull IdCooldownComponent get(PlayerEntity player) {
		return MiaLib.ID_COOLDOWN_COMPONENT.get(player);
	}

	public static void sync(PlayerEntity player) {
		MiaLib.ID_COOLDOWN_COMPONENT.sync(player);
	}

	@Override
	public void tick() {
		for (var id : this.cooldowns.keySet()) {
			if (this.cooldowns.get(id).endTick > this.tick) {
                this.cooldowns.remove(id);
				sync(this.player);
			}
		}
	}

	public boolean isCoolingDown(Identifier id) {
		return this.cooldowns.containsKey(id);
	}

	public int getCooldown(Identifier id) {
		var entry = this.cooldowns.get(id);
		if (entry == null) {
			return 0;
		}
		return entry.endTick - entry.startTick;
	}

	public float getCooldown(Identifier id, float tickDelta) {
		if (!this.cooldowns.containsKey(id)) {
			return 0.0f;
		}
		var entry = this.cooldowns.get(id);
		var f = entry.endTick - entry.startTick;
		var g = entry.endTick - this.tick + tickDelta;
		return MathHelper.clamp(g / f, 0.0F, 1.0F);
	}

	public void setCooldown(Identifier id, int ticks) {
		if (ticks <= 0) {
            if (this.cooldowns.remove(id) != null) {
				sync(this.player);
			}
			return;
		}
        this.cooldowns.put(id, new Entry(this.tick, this.tick + ticks));
		sync(this.player);
	}

	public static boolean isCoolingDown(PlayerEntity player, Identifier id) {
		return get(player).isCoolingDown(id);
	}

	public static void setCooldown(PlayerEntity player, Identifier id, int ticks) {
		get(player).setCooldown(id, ticks);
	}

	public static int getCooldown(PlayerEntity player, Identifier id) {
		return get(player).getCooldown(id);
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound tag) {
		var compound = tag.getCompound("cooldowns");
		if (compound == null) return;
        this.cooldowns.clear();
		for (var id : compound.getKeys()) {
			var entry = compound.getCompound(id);
			if (entry != null) this.cooldowns.put(new Identifier(id), new Entry(entry.getInt("start"), entry.getInt("end")));
		}
		this.tick = tag.getInt("tick");
	}

	@Override
	public void writeToNbt(NbtCompound tag) {
		var compound = new NbtCompound();
		for (var id : this.cooldowns.keySet()) {
			var entry = new NbtCompound();
			entry.putInt("start", this.cooldowns.get(id).startTick);
			entry.putInt("end", this.cooldowns.get(id).endTick);
			compound.put(id.toString(), entry);
		}
		tag.put("cooldowns", compound);
		tag.putInt("tick", this.tick);
	}

	record Entry(int startTick, int endTick) {}
}