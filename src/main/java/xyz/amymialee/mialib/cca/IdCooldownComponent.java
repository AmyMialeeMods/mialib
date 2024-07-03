package xyz.amymialee.mialib.cca;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistry;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;
import xyz.amymialee.mialib.Mialib;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores cooldowns tied to identifiers.
 */
public class IdCooldownComponent implements AutoSyncedComponent, CommonTickingComponent {
	public static final ComponentKey<IdCooldownComponent> KEY = ComponentRegistry.getOrCreate(Mialib.id("identifier_cooldown"), IdCooldownComponent.class);
	private final PlayerEntity player;
	private final Map<Identifier, Entry> cooldowns = new HashMap<>();
	private int tick;

	public IdCooldownComponent(PlayerEntity player) {
		this.player = player;
	}

	public void sync() {
		KEY.sync(this.player);
	}

	@Override
	public void tick() {
		this.tick++;
		for (var entry : this.cooldowns.entrySet()) {
			if (entry.getValue().endTick < this.tick) {
                this.cooldowns.remove(entry.getKey());
                this.sync();
			}
		}
	}

	public boolean isCoolingDown(Identifier id) {
		return this.cooldowns.containsKey(id);
	}

	/**
	 * @return Number of ticks remaining on the cooldown.
	 */
	public int getCooldown(Identifier id) {
		var entry = this.cooldowns.get(id);
		if (entry == null) {
			return 0;
		}
		return entry.endTick - entry.startTick;
	}

	/**
	 * @return A value between 0.0 and 1.0 representing the cooldown progress.
	 */
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
                this.sync();
			}
			return;
		}
        this.cooldowns.put(id, new Entry(this.tick, this.tick + ticks));
        this.sync();
	}

	@Override
	public void readFromNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		this.tick = tag.getInt("tick");
		this.cooldowns.clear();
		var compound = tag.getCompound("cooldowns");
		if (compound == null) return;
		for (var id : compound.getKeys()) {
			var entry = compound.getCompound(id);
			if (entry != null) this.cooldowns.put(Identifier.of(id), new Entry(entry.getInt("start"), entry.getInt("end")));
		}
	}

	@Override
	public void writeToNbt(@NotNull NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
		tag.putInt("tick", this.tick);
		var compound = new NbtCompound();
		for (var id : this.cooldowns.keySet()) {
			var entry = new NbtCompound();
			entry.putInt("start", this.cooldowns.get(id).startTick);
			entry.putInt("end", this.cooldowns.get(id).endTick);
			compound.put(id.toString(), entry);
		}
		tag.put("cooldowns", compound);
	}

	@Override
	public boolean isRequiredOnClient() {
		return false;
	}

	private record Entry(int startTick, int endTick) {}
}